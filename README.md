# TicketManager

**TicketManager** is a powerful and customizable plugin for Minecraft servers, designed to manage player tickets efficiently. With support for both MySQL databases and local file storage, TicketManager provides an intuitive interface for players and administrators to create, assign, and resolve tickets.

## Features

- **Ticket Creation and Management**: Players can easily create tickets to report issues, ask questions, or request help.
- **Database Support**: Fully supports MySQL for storing tickets, with an option for local file storage.
- **Customizable Notifications**: Notify players when their tickets are assigned, closed, or escalated.
- **Permissions Integration**: Integrates with common permissions plugins to control access to ticket commands.
- **Automated Ticket Closure**: Option to automatically close inactive tickets after a configurable period.
- **Logging**: Logs all ticket actions to the console and/or a file, with customizable log levels.

## Installation

To install the TicketManager plugin on your Minecraft server:

1. **Download the Plugin**: Download the latest version of TicketManager from the [Releases](https://github.com/your-repo/ticketmanager/releases) page.
2. **Place in Plugins Folder**: Place the downloaded `.jar` file into your server's `plugins` folder.
3. **Start or Restart the Server**: Start or restart your server to load the plugin.
4. **Configure the Plugin**: After the server starts, a `TicketManager` folder will be created in your `plugins` directory. Customize the `config.yml` and `messages.yml` files to suit your server’s needs.
5. **Set Up Permissions**: Configure your permissions plugin to allow access to TicketManager commands as needed.

## Configuration

TicketManager is highly configurable via the `config.yml` and `messages.yml` files. Here’s a brief overview of key configuration options:

### `config.yml`
- **Database Configuration**: Set up your MySQL database connection.
- **Plugin Settings**: Enable or disable database usage, set automatic ticket closure, and more.
- **Ticket Defaults**: Customize default priorities, status, and limits for player tickets.
- **Notifications**: Configure messages sent to players when their tickets are created, assigned, or closed.
- **Logging**: Control how and where ticket actions are logged.

### `messages.yml`
- **Custom Messages**: Customize every message sent by the plugin, including ticket notifications, help commands, and more.
- **Placeholders**: Use placeholders like `{creator}`, `{id}`, and `{status}` to dynamically insert ticket data into messages.

## Usage

TicketManager provides several commands for both players and administrators:

- **/ticket create `<message>`**: Create a new ticket with the specified message.
- **/ticket list**: List all open tickets.
- **/ticket assign `<id> <player>`**: Assign a ticket to a specific player.
- **/ticket close `<id>`**: Close a specific ticket.
- **/ticket reload**: Reload the plugin configuration files.

### Example Workflow
1. **Player**: `/ticket create "Help! I'm stuck!"` - Creates a new ticket.
2. **Admin**: `/ticket list` - Lists all open tickets.
3. **Admin**: `/ticket assign 1 AdminName` - Assigns the ticket to an admin.
4. **Admin**: `/ticket close 1` - Resolves and closes the ticket.

## Permissions

TicketManager uses permissions to control access to its commands:

- **ticketmanager.create**: Allows players to create tickets.
- **ticketmanager.list**: Allows viewing of all open tickets.
- **ticketmanager.assign**: Allows assigning tickets to players.
- **ticketmanager.close**: Allows closing of tickets.
- **ticketmanager.reload**: Allows reloading of the plugin configuration.

## Contributing

Contributions to TicketManager are welcome! Here’s how you can contribute:

1. **Fork the Repository**: Click on the "Fork" button at the top right of this page to create your own copy of the repository.
2. **Create a New Branch**: Use `git checkout -b my-feature-branch` to create a new branch for your feature or bug fix.
3. **Make Your Changes**: Make your changes and commit them with clear commit messages.
4. **Push to Your Branch**: Use `git push origin my-feature-branch` to push your changes to your fork.
5. **Submit a Pull Request**: Open a pull request on GitHub, detailing the changes you made and why they should be merged.

## Issues and Support

If you encounter any issues with TicketManager or need help, please open an issue on the [GitHub Issues](https://github.com/ticketmanager/ticketmanager/issues) page. We welcome bug reports, feature requests, and general questions.

## License

TicketManager is licensed under the MIT License. See the [LICENSE]([https://github.com/your-repo/ticketmanager/blob/main/LICENSE](https://github.com/BaconDrips/TicketManager/tree/main?tab=MIT-1-ov-file#readme)) file for more information.

## Acknowledgments

- Thanks to all contributors and users of TicketManager who have helped improve the project!
