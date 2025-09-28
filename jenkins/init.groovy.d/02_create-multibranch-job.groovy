import jenkins.model.*
import org.jenkinsci.plugins.github_branch_source.*
import jenkins.branch.*
import org.jenkinsci.plugins.workflow.multibranch.*

def env = System.getenv()
def instance = Jenkins.get()

def githubRepo    = env['GITHUB_REPOSITORY']
def credentialsId = "github-creds"

println "--> Запуск create-multibranch-job.groovy"

if (!githubRepo) {
    println "Переменная окружения GITHUB_REPOSITORY не задана (пример: owner/repo)"
    return
}

println "--> GITHUB_REPOSITORY = ${githubRepo}"

// Разбиваем owner/repo
def parts = githubRepo.split('/')
if (parts.length != 2) {
    println "Неверный формат GITHUB_REPOSITORY. Ожидалось: owner/repo"
    return
}
def owner = parts[0]
def repo  = parts[1]

// Создаём GitHub SCM Source
def source = new GitHubSCMSource(owner, repo)
source.setCredentialsId(credentialsId)
source.setTraits([
        new BranchDiscoveryTrait(1),
        new OriginPullRequestDiscoveryTrait(1),
        new ForkPullRequestDiscoveryTrait(1, new ForkPullRequestDiscoveryTrait.TrustPermission())
])

def branchSource = new BranchSource(source)
branchSource.setStrategy(new DefaultBranchPropertyStrategy([] as BranchProperty[]))

def jobNames = ["bankapp", "keycloak", "accounts", "blocker", "cash", "exchange", "exchange_generator", "notifications", "transfer", "ui", "kafka"]
jobNames.eachWithIndex { jobName, idx ->
    // Проверка, существует ли уже такой job
    if (instance.getItem(jobName) != null) {
        println "--> Multibranch job '${jobName}' уже существует. Пропускаем."
    } else {
        def mbp = new WorkflowMultiBranchProject(instance, jobName)
        mbp.getSourcesList().add(branchSource)

        def factory = new WorkflowBranchProjectFactory()
        def scriptPath = "jenkins/jenkinsfiles/${jobName}.Jenkinsfile"
        factory.setScriptPath(scriptPath)
        mbp.setProjectFactory(factory)

        instance.add(mbp, jobName)
        mbp.save()
        if ("bankapp".equals(jobName)) {
            mbp.scheduleBuild2(0)
            println "--> Multibranch job '${jobName}' создан и запущен на '${githubRepo}'"
        } else {
            println "--> Multibranch job '${jobName}' создан"
        }
    }
}