# AFKMania

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.4-brightgreen)
![License](https://img.shields.io/badge/License-CC0-blue.svg)
![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen)

AFKMania is a versatile and customizable AFK (Away From Keyboard) system for Minecraft 1.20.4 servers. It provides multiple modules to choose from, including a WorldGuard region-based system that allows you to set specific rewards for players who stay AFK in designated areas.

## Features

- **Multiple AFK Modules**: Choose from various AFK systems to suit your server's needs.
- **WorldGuard Integration**: Use WorldGuard regions to define AFK zones.
- **Custom Rewards**: Set custom rewards for players who stay AFK in specified regions.
- **User-Friendly**: Easy to set up and configure.
- **Folia Support**: Can use this plugin with Folia! (Barely Tested)

## Getting Started

### Prerequisites

- Minecraft server running version 1.20.4
- WorldGuard plugin (if using the AFK_Pool module)

### Installation

1. **Download the Plugin:**
   Download the latest version of AFKMania from the [Releases](https://github.com/PizzaThatCodes/AFKMania/releases) page.

2. **Install the Plugin:**
   Place the downloaded `AFKMania.jar` file in your server's `plugins` directory.

3. **Restart the Server:**
   Restart your Minecraft server to load the plugin.

### Configuration

AFKMania provides a configuration file to customize the AFK modules and rewards.

1. **Locate the Configuration File:**
   After the first start, the configuration file will be generated in the `plugins/AFKMania` directory.

2. **Edit the Configuration File:**
   Open the `config.yml` file with a text editor and configure the settings to your preference.

Below is an example configuration of `modules/afk_pool.yml`:

```
afk_pools:
  afk_pool:
    region_name: "afk_pool"
    rewards:
      - "Diamond"
      - "Emerald"
reward_interval_seconds: 5
afk-message:
  inventory-collect:
    title: "&6AFK Pools"
    subtitle: "&aGenerating Rewards..."
  inventory-full:
    title: "&6AFK Pools"
    subtitle: "&cYour inventory is full"
  leaving-afk:
    title: "&6AFK Pools"
    subtitle: "&cYou are no longer AFK"
    send-message:
      enabled: true
      message: "%prefix% You are no longer in the AFK pool region. You were in the region for {seconds} seconds."
rewards:
  Diamond:
    command: "give %player% minecraft:diamond 1"
    chance: 10
  Emerald:
    command: "give %player% minecraft:emerald 1"
    chance: 30
   ```

### Commands and Permissions

- `/afkmania reload` - Reload the plugin configuration.
  - Permission: `afkmania.reload`

### Permissions

- `afkmania.reload` - Allows players to reload the plugin's config files.

## Contributing

We welcome contributions from the community! To contribute:

1. Fork the repository.
2. Create a new branch with your feature or bugfix.
3. Submit a pull request.

## License

This project is licensed under the CC0 License - see the [LICENSE](LICENSE) file for details.

## Contact

For support and inquiries, please join our [Discord server](https://discord.gg/pj8auQEPJU) or open an issue on GitHub.

---

Thank you for using AFKMania! We hope it enhances your Minecraft server experience.
