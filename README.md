# Asynchronous symmetric peer-to-peer encrypted messaging via SMTP under Android.

Email app for Android and library using headers to establish secret communication

The purpose of this project is to:

- Provide end-to-end encryption and
- Hide encryption details from the user

Initially, the project uses custom email headers to transmit Diffie-Hellman parameters. After the DH exchange happens, both users calculate the secret key and use it to encrypt and decrypt the email message with AES.
