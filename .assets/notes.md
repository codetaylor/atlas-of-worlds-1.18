### When the Atlas Device open portal button is clicked:
- Unregister previous dimension
- Delete dimension files
- Clear all portal block data in range
- Create a dimension
- Transfer map item data to world saved data
- Spawn the structure
- Store the dimension key
- Locate the spawn
- Activate N portal blocks in range

### When the Atlas Device is broken:
- Unregister previous dimension
- Delete dimension files
- Clear all portal block data in range

### When a portal block is broken:
- Clear the portal block data

### What kind of special handling is required when all portal blocks are broken?
- Nothing.
- The player will never be able to get back into the dimension.
- The dimension will be unregistered and deleted when the Atlas Device is next used or broken.

### What kind of special handling is required when a player opens a map and never triggers a cleanup?
- Timed cleanup; a map will only last so long without any players in it before it is removed by the system.
- Portal blocks tick and deactivate if bound to a non-existent dimension