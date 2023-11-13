# Sky-take-out

First, create a privacy profile named **application-dev.yml** under the path of *sky-server\src\main\resources*. Currently, the file format is as follows:
```yml
sky:
  datasource:
    driver-class-name: your driver-class-name
    host: your host
    port: your port
    database: your dataset name
    username: your username
    password: your password
  alioss:
    endpoint: your alioss endpoint
    bucket-name: your alioss bucket-nam
    access-key-id: your alioss access-key-id
    access-key-secret: your alioss access-key-secret
  redis:
    host: your redis host
    port: your redis port
    password: your redis password
    database: your redis database
  wechat:
    appid: your wechat appid
    secret: your wechat secret
```