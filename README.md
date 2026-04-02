<p align="center">
  <img src="images/logo.png" />
</p>
<div align="center">
  <h1>✨ Get in contact ✨</h1>
  <p>
    Join <strong>Cybernetic-Forge</strong> on Discord<br>
    for support and discussions!
  </p>

  <a href="https://discord.gg/qewNHejdMT">
    <img src="https://img.shields.io/badge/Join%20Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white" alt="Join Discord">
  </a>
</div>

# Introduction into Grimoires
**Grimoires** is a plugin that uses books with the aim of utilising their functionality to its limits.
Immerse yourself in the magical world of books and allow Minecraft to go beyond the limits of what 
was previously possible with books.

## Features
☄️Use Chiseled-Bookshelves like a gui<br>
☄️Write books and publish those through an gui (even with pricing)<br>
☄️Add more than one author to a published book<br>

## W.I.P. Features
☄️Add a way to create custom books with mysteries inside of it (decryption tasks for rewards) <br>
☄️Interact and integrate with other plugins like Fabled, MythicMobs and MagicSpells<br>
☄️Use the API to fully customize your options with Grimoires<br>


### Definition of Done (Initial Publication)
- [x] Players can open a chiseled-bookshelf and manage a gui with the books that are inside of it<br>
- [x] Players can publish books through a gui and set a price for it<br>
- [x] Players can add more than one author to a published book. Configurable<br>
- [x] Players can interact with the digital bookstore and buy books that are published (`/grimoire store` / `/bookstore`)<br>
- [x] Admins can toggle whether books are obtained virtually or physically (`buyType: virtual|physical` in `BookStorage.yml`)<br>
  - [x] Physical: Books are obtained as items and need rebuying them if lost (Recover Lost Books GUI in `/bookstore`)<br>
  - [x] Virtual: Books are obtained virtually and can be read everywhere at any time<br>
- [x] When virtual, players can see their personal book-storage and read them from there (`/grimoire show` filtered to owned/free books)<br>
- [x] Full SQLite and MySQL/MariaDB support<br>
- [x] Full API for Grimoires (at least basic features) — custom Bukkit events in `modules/api/events/`<br>