# Как запустить
* ```docker compose up```
* открыть в брауезере http://localhost:8080/

# Как запустить локально вручную
1.
```bash
minikube start
```
```bash
chmod +x ./build_images.sh
```
```bash
./build_images.sh
```
```bash
chmod +x ./load_images.sh
```
```bash
./load_images.sh
```
```bash
helm upgrade --install --atomic keycloak ./helm_charts/charts/keycloak
```
```bash
helm uninstall keycloak
```
```bash
helm upgrade --install --atomic notifications ./helm_charts/charts/notifications
```
```bash
helm uninstall notifications
```
```bash
helm upgrade --install --atomic blocker ./helm_charts/charts/blocker
```
```bash
helm uninstall blocker
```
1. Проверка установки
```bash
kubectl get pods
``` 
(дождаться пока все поды будут в состоянии Running)
```bash
kubectl get svc
kubectl get ingress
```
```bash
minikube image ls
```
```bash
minikube tunnel
```