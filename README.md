# Pactime Multiplayer
## Enables multiplayer mode for pactime game: https://github.com/valentynhol/pactime
### Paths:
- `GET /lobbies` - display joinable list of lobbies
- `POST /lobbies` - create new lobby
- `DELETE /lobbies` - delete existing lobby
- `POST /lobbies/join` - join lobby
- `POST /lobbies/leave` - leave lobby
- `POST /lobbies/{code}/start` - start game in lobby

### By default, starts on: `http://localhost:8080/`
### SebSocket urls: `ws://localhost:8080/ws/lobbies/{code}`