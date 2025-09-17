# Как запустить в Jenkins
* ```docker compose up```
* открываем в брауезере http://localhost:8888/

# Как запустить локально вручную
1. запускаем minikube
```bash
minikube start
```
2. собираем docker-образы
```bash
chmod +x ./build_images.sh
```
```bash
./build_images.sh
```
3. загружаем docker-образы в minikube
```bash
chmod +x ./load_images.sh
```
```bash
./load_images.sh
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
helm install bankapp-test ./helm_charts -n test
```
3. добавляем перенаправление внутрь кластера в namespace test
```bash
kubectl --namespace test port-forward service/ui 8888:8080
```
4. открываем в брауезере http://localhost:8888/
5. деинсталируем umbrella chart из namespace test
```bash
helm uninstall bankapp-test -n test
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
```bash
minikube image ls
```