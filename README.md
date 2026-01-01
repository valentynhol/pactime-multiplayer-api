# Pactime Multiplayer

## Enables multiplayer mode for pactime game: https://github.com/valentynhol/pactime

### Docs can be accessed on `/` endpoint

### By default, starts on: `http://localhost:8080/`

### SebSocket urls: `ws://localhost:8080/ws/lobbies/{code}`

### Example environment variables:
```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://url.example.com/db
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin123

SECURITY_JWT_SECRET=<random_base64_secret>
SECURITY_GOOGLE_CLIENT_ID=123456-abcdef123.apps.googleusercontent.com
SECURITY_GOOGLE_CLIENT_SECRET=qwerty-123123qwerty

CONFIG_API_URL=http://localhost:8080
```
`SECURITY_JWT_SECRET` may be generated using:
```shell
openssl rand -base64 32
```
