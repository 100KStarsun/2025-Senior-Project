# Agora

## CSDS 395 - Senior Project Spring 2025

### Team Members

- Adriana
- Avalon
- Jaiden
- Jennifer
- Levi
- Sarah
- Stephen

---

### Versioning and Dependencies

- Java 21.0.6

### Repository Guidelines

Naming Conventions:

- Classes and Interfaces: PascalCase
    - Use capital letters at the start of each word, **including** the first word of the Class or Interface.

    ```java
    public Interface Cat {
        // Add cat-related methods here
    }

    public Class MaineCoone implements Cat {
        // Act like a cat
    }
    ```

- Methods and Variables: camelCase
    - Use capital letters at the start of each word, **exlcuding** the first word of the Method or Variable.

    ```java
    private String catName;
    
    private shedEverywhere(boolean shouldAlsoMakeHairball) {
        // make a mess
    }
    ```

- Constants: SCREAMING_SNAKE_CASE
    - Use all caps, with words seperated by underscores.

    ```java
    public static final int MAXIMUM_CATS_IN_HOUSEHOLD = 7
    ```
Commenting Conventions:

- Utilize javadoc comments for each method of a class
- These will be parsed to be included in our documentation webpage

- Example:
  ```java
    /**
    * @brief This method determines whether a cat is shedding based on inputs
    *
    * @param hairy whether the cat is hairy
    * @param hairType the type of hair the cat has
    *
    * @return whether the cat is shedding
    */
    public boolean isShedding(boolean hairy, int hairType) {
        // method
    }
    ```

Repo Conventions:

- Utilize unique branches for each unique feature; do not re-use a branch that was previously merged
- All branches should be derived from `dev`, and all branches should merge back into `dev`.
    - `dev` will be merged into `main` by Levi or Avalon on a weekly basis, by 11:30 AM on the Tuesday of each sprint.
        - At this point, `dev` and `main` will be identical.

Coding Conventions:

- Try to use constants that are well defined where possible
    - This makes it *much* easier to figure out what code does, and it helps whoever is looking at your code debug and understand why you made the choices you did

## Server Stuff

### Key

- `(<data_type>)`: the data type of the field
- `{Partition_key}`: specifies that this field is the parition key of the item for that table
- `{Sort_key}`: specifies that this field is the sort key of the item for that table
- `{key_x}`/`{value_x}`: specifies that a map has more than one item, but a known number of items
- `{key_n}`/`{value_n}`: specifies that a map has more than one item, but an unknown/dynamic number of items

### Tables

- Everything will be encoded as a base64 string before being encrypted (if necessary) and then sent to the database
    - This means that the only human-readable information in the database will be the partition and sort keys of a table
    - The key for this base64 string will be `data`, the value will be of type String
    - Due to how Java handles object encoding, the partition and sort keys will be included in the base64 string of data

#### agora_users

This table's key is the user's username. This field is unique to each user and no user is allowed to have the same username as someone else (because it's their case id). No sort key because username is easy, no email field because we can use `username`@case.edu. Each user's display name is their preferred first name and legal last name, followed by a # with the user's username, so people can confirm who they are talking to. This table will have the following fields:

- `username(String){Partition_key}`: the user's username
- `preferred_first(String)`: User's preferred first name
- `legal_first(String)`: User's legal first name
- `last(String)`: User's last name
- `time_created(Number)`: unix timestamp for when profile was created
- `num_swaps(Number)`: the number of "Swapps" includes the number of services provided and goods sold. (Things coming from you)
- `swapp_rating(Number)`: The "rating" that people have given you
- `payment_methods(Map)`: List of payment methods the user has setup
    - `{key_x}payment_method_name: {value_x}(Boolean)`: the key is the name of the payment method and the value is true or false depending on if the user has set it up
- `chats(Map)`: List of usernames and chat_ids associated with the username
    - `{key_n}username: {value_n}(List)`: the key for each Map entry is the username that's part of the chat and the value is a list containing all of the `chat_UUID`s that make up the chat history of the two users
- `published_listings(List)`: List of all UUIDs of listings that the user has published on their page
- `drafted_listings(List)`: List of all UUIDs of listings that the user has drafts for
- `liked_listings(List)`: List of all UUIDs of listings that the user has "liked"/"favorited"
- `blocked_users(List)`: List of all UUIDs of users that have been blocked by this user
- `muted_users(List)`: List of all UUIDs of users that have been muted by this user

#### agora_passwords

This table stores the passwords for logins. This table's partition key is the hash of a specific combination of static user data. This table will have the following fields:

- `hashcode(String){Partition_key}`: the hashcode of the static user data
- `password(String)`: an encrypted version of the user's password

#### agora_chats

This table's key is the chat_id. Chat_ids are generated sequentially. I don't think the chat_id is potentially sensitive information so I don't believe we need to make that secure. Chat messages are not encrypted yet but they will be eventually, even when stored (this means that we cannot view these messages in plaintext thru AWS). This table will have the following fields:

- `chat_UUID(String){Partition_key}`: the UUID of this chat block
- `usernames_pair(String){Sort_key}`: the pair of usernames associated with this chat, of the form `(username_1, username_2)`, where `username_1` is the person who sent the first message
- `order(Number)`: the number representing how many chat elements have had to be created for these two users; required to put chats in correct order on user side; start at 0
- `messages(List)`: a list of maps, where each map represents a message
    - `message_id(Map)`: the nth message sent between the two users in this chat block (there will be many entries of these maps)
        - `{key_n}text: {value_n}(String)`: the text of the message (encrypted)
        - `{key_n}timestamp: {value_n}(Number)`: the unix timestamp of the message
        - `{key_n}from_first: {value_n}(Boolean)`: whether or not the message is from the person who sent the first message in this chat

#### agora_listings

This table's key is the UUID field of an item or service. The sort key is the name of the listing (this way multiple users can name their stuff the same thing). This table will have the following fields:

- `listing_UUID(String){Partition_key}`: the UUID of the particular item
- `title(String){Sort_key}`: the name of the agora listing
- `listing_time(Number)`: the unix timestamp of when this listing was posted
- `price(Number)`: the price of the listing, in USD
- `description(String)`: the seller's description of the listing
- `seller_UUID(String)`: the seller's UUID
- `seller_name(String)`: the seller's display name
- `seller_username(String)`: the seller's username
- `type(String)`: the type of listing (good/service) (left as a string so we could add more later)
- `quantity(Number)`: the number of this product/service available (only used if `infinite_available` is set to false)
- `infinite_available(Boolean)`: whether or not there are unlimited times this service can be redeemed (option is only available for services, not for goods, default: false)
- `is_published(Boolean)`: whether or not this listing has been published to the Agora
- `is_accepting_cash(Boolean)`: whether or not the user will accept cash for this listing
- `is_tradable(Boolean)`: whether or not the user is willing to trade for another item
- `past_buyers(List)`: A list of UUIDs of previous buyers

### Documentation

Documentation for this project is generated using Doxygen. Our project site can be viewed at:
https://codedocs.xyz/100KStarsun/2025-Senior-Project/index.html
