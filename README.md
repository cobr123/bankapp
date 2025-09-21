# Как запустить в Jenkins
1. включаем Kubernetes в Docker Desktop (настройка → Kubernetes → Enable Kubernetes)
2. Создайте файл `jenkins_kubeconfig.yaml`. Jenkins будет использовать этот файл для доступа к Kubernetes.
```bash
kubectl config view --flatten --minify > jenkins_kubeconfig.yaml
```
Затем отредактируйте файл:
**Замените `server: https://127.0.0.1:6443` на:**
```yaml
server: https://host.docker.internal:6443
```
**Добавьте:**
```yaml
insecure-skip-tls-verify: true
```
Это нужно, чтобы Jenkins внутри контейнера смог обратиться к вашему локальному кластеру и проигнорировал самоподписанные сертификаты.

3. в файле jenkins/.env укажите GITHUB_TOKEN чтобы не ждать лимиты на запросы
4. запускаем jenkins в докере
```bash
docker compose -f ./jenkins/docker-compose.yml up
```
5. добавляем перенаправление внутрь кластера
```bash
kubectl --namespace test port-forward service/ui 8888:8080
```
6. открываем в брауезере http://localhost:8888/

# Как запустить локально вручную
1. включаем Kubernetes в Docker Desktop (настройка → Kubernetes → Enable Kubernetes)
2. собираем docker-образы
```bash
chmod +x ./build_images.sh
```
```bash
./build_images.sh
```
3. собираем зависимости umbrella chart
```bash
helm dependency build ./helm_charts
```
4. деплоим umbrella chart
```bash
helm install bankapp ./helm_charts
```
5. добавляем перенаправление внутрь кластера
```bash
kubectl --namespace default port-forward service/ui 8888:8080
```
6. открываем в брауезере http://localhost:8888/
7. деинсталируем umbrella chart
```bash
helm uninstall bankapp
```

# Как запустить локально вручную в другом namespace
1. создаем namespace test
```bash
kubectl create namespace test
```
2. деплоим umbrella chart в namespace test
```bash
helm install bankapp ./helm_charts -n test
```
3. добавляем перенаправление внутрь кластера в namespace test
```bash
kubectl --namespace test port-forward service/ui 8888:8080
```
4. открываем в брауезере http://localhost:8888/
5. деинсталируем umbrella chart из namespace test
```bash
helm uninstall bankapp -n test
```

# Проверка установки
```bash
kubectl get pods --all-namespaces
``` 
Также можно посмотреть логи внутри пода
```bash
kubectl logs -f имя_пода
```
(дождаться пока все поды будут в состоянии Running)
```bash
kubectl get svc
kubectl get pods
kubectl get ingress
```