# AWS DynamoDB README

## Key

- `(<data_type>)`: the data type of the field
- `{Partition_key}`: specifies that this field is the parition key of the item for that table
- `{Sort_key}`: specifies that this field is the sort key of the item for that table
- `{key_x}`/`{value_x}`: specifies that a map has more than one item, but a known number of items
- `{key_n}`/`{value_n}`: specifies that a map has more than one item, but an unknown/dynamic number of items

## Tables

### agora_users

This table's key is the user's username. This field is unique to each user and no user is allowed to have the same username as someone else. A user's username is their university_id. (For CWRU, it's their network_id.) Each user's display name is their preferred first name and legal last name, followed by a # with the user's username, so people can confirm who they are talking to. This table will have the following fields:

- `username(String){Partition_key}`: the user's username
- `UUID(String){Sort_key}`: UUID for the user
- `preferred_first(String)`: User's preferred first name
- `legal_first(String)`: User's legal first name
- `last(String)`: User's last name
- `email(String)`: user's school email address
- `num_swaps(Number)`: the number of "Swapps" includes the number of services provided and goods sold. (Things coming from you)
- `swapp_rating(Number)`: The "rating" that people have given you
- `university(String)`: The name of the university you are associated with
- `payment_methods(Map)`: List of payment methods the user has setup
    - `{key_x}payment_method_name(String)`: the name of the payment method (e.g. Venmo, Zelle)
        - `{value_x}(Boolean)`: Whether or not the user has the specific payment method setup
- `chats(Map)`: List of usernames and chat_ids associated with the username
    - `{key_n}username(String)`: the key for each Map entry is the username that's part of the chat
        - `{value_n}(List)`: the `chat_id`s associated with the two users (if chat history is too big for one DynamoDB entry we need to be able to handle multiple)
- `goods_services(Map)`: List of goods and services the user has on their profile
    - `{key_n}item_service_name(String)`: the title listing of the good or service
        - `{value_n}(Number)`: the id of the good or service published

### agora_chats

This table's key is the chat_id. Chat_ids are generated sequentially. I don't think the chat_id is potentially sensitive information so I don't believe we need to make that secure. Chat messages are not encrypted yet but they will be eventually, even when stored (this means that we cannot view these messages in plaintext thru AWS). This table will have the following fields:

- `chat_id(Number){Partition_key}`: the index of this chat
- `username_pair(String){Sort_key}`: the pair of usernames associated with this chat, of the form `(username_1, username_2)`
- `message_id(Map)`: the nth message sent between the two users
    - `{key_n}text(String)`: the field name for the message text
        - `{value_n}(String)`: the actual text of the nth message
    - `{key_n}timestamp(String)`: the fieldname for the timestamp
        - `{value_n}(Number)`: the unix timestamp, in seconds