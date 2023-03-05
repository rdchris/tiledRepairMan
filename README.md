# tiledRepairMan

#### Note Windows Impl only

This small utility program is to resolve issue swith globalId + LocalId collisions in TMX maps. This can occur if you
add
tiles or objects to a tileset which will increment the localId, thus their localId + globalID can collide with another
tilesets.

for example

> javax.xml.stream.XMLStreamException: Tileset with first global ID 10852 contains a tile with local ID 8 and thus
> global ID 10860, conflicting with a tile with that same global ID from another tileset


We found that resaving using tiled resolved that issue, thus we wrote this small utility program to automate the process
of resaving the files. NOTE: You must save as the TMX file as a new file, simply overriding the old files doesn't cause
tiled to updated the local Ids + global Ids.