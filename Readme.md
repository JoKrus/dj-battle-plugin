# DJ-Battle Plugin

Minecraft Spigot Plugin developed mainly for usage on the DJ-Battle Server but can be used elsewhere with ease

## Features

### Teams

Create, join and use teams via the `/djteam` command

### Spectator Mode

Spectate your teammates and the battle automatically.

- Switch targets via `/djspec`
- Fly around as usual after your team is eliminated

### API

Supports events via `BattleStartedEvent`and `BattleStoppedEvent`
from [BattlePluginApi](https://github.com/JoKrus/dj-battle-plugin-api)

### Configurable

Configure the lobby location, spawn location, grace period, map size and map shrink duration easily via a config file

## Usage

### Setup

- Use `/djbattle init` and `/djbattle reload` to apply your config to the server and setup the environment
- Let every player setup their teams via `/djteam join`
- Test if every player is in a team via `/djteam test`

### Start

- Use `/djbattle start` to start a battle

### Stop

- If the battle should be stopped, use `/djbattle stop`
    - The battle will not be stopped automatically!
    - It will check after `stop` if only 1 team is left and declare that team as a winner though.

## Building the jar

### Prerequisites

- Java 17+
- Maven 3+

### Build

- ```git checkout https://github.com/JoKrus/dj-battle-plugin.git```
- ```cd dj-battle-plugin```
- ```mvn package```
- `target/BattlePlugin-{Version}.jar` is the built plugin.
- Copy that into your `plugins/` directory on your Spigot Server

## Contribution

At the moment, it is not planned to take contributions. If you discover an issue or have a feature request,
you can always create an issue.

If the feature fits into the plugin, we can discuss implementation of it.