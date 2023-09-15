package io.eliotesta98.VanillaChallenges.api;

import io.eliotesta98.VanillaChallenges.Core.Main;

/**
 * VanillaChallenges API.
 *
 * @author eliotesta98
 * @version 1.1.8
 */
public class VanillaChallengesAPI {
    //private static final boolean isDebugEnabled = Main.instance.getConfigGestion().getDebug().get("API");

    /**
     * Returns the ID of the CubeGenerator from the location of the block or -1 if not found.
     *
     * @param blockLocation location of the block.
     * @return generator id or -1 if not found.
     *//*
    public static int getGeneratorIdFromLocation(Location blockLocation) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();
        Coordinate coordinate = Main.db.checkIfBlockIsAGeneratorBlock(blockLocation, "PLACED");
        // controllo se è vuoto
        if (coordinate == null) {
            if (isDebugEnabled) {
                debug.addLine("Method: getGeneratorIdFromLocation");
                debug.addLine("Coordinate is null");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return -1;
        } else {
            if (isDebugEnabled) {
                debug.addLine("Method: getGeneratorIdFromLocation");
                debug.addLine("Generator id:" + coordinate.getId_generatore());
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return coordinate.getId_generatore();
        }
    }
*/
    /**
     * Returns the Owner nickname of the CubeGenerator from the id of the generator.<br/>
     * If GeneratorOwnerControl config option is disabled, it returns "controlOwnerDisabled".<br/>
     * If not found, it returns blank.<br/>
     *
     * @param generatorId id of the CubeGenerator.
     * @return <b>Generator Owner nickname</b> if GeneratorOwnerControl config option is active.
     * <br/><b>"controlOwnerDisabled"</b> if GeneratorOwnerControl config option is disabled.
     * <br/><b>Blank</b> if not found.
     *//*
    public static String getGeneratorOwnerFromId(int generatorId) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();
        Generator generator = Main.db.getPlayerGeneratorsById(generatorId);
        if (!Main.instance.getConfigGestion().isGeneratorOwnerControl()) {
            if (isDebugEnabled) {
                debug.addLine("Method: getGeneratorOwnerFromId");
                debug.addLine("Control of owner disabled");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return "controlOwnerDisabled";
        }
        // controllo se è vuoto
        if (generator == null) {
            if (isDebugEnabled) {
                debug.addLine("Method: getGeneratorOwnerFromId");
                debug.addLine("Generator is null");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return "";
        } else {
            if (isDebugEnabled) {
                debug.addLine("Method: getGeneratorOwnerFromId");
                debug.addLine("Owner: " + generator.getOwner());
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return generator.getOwner();
        }
    }
*/
    /**
     * Check if the provided nickname is the owner of the provided generatorId. <br/>
     * If the generatorId is invalid or the owner nickname is invalid, the return is false.
     *
     * @param nickname    nickname of the owner to check.
     * @param generatorId id of the CubeGenerator to check.
     * @return if the provided nickname is the owner of the provided generatorId.
     *//*
    public static boolean isGeneratorOwner(String nickname, int generatorId) {
        return isAGenerator(nickname, generatorId);
    }
*/
    /**
     * Check if the provided nickname is the owner of the provided generator id. <br/>
     * If the generatorId is invalid or the owner nickname is invalid, the return is false.
     *
     * @param nickname    nickname of the owner to check.
     * @param generatorId id of the CubeGenerator.
     * @return if the provided nickname is the owner of the provided generatorId.
     * @deprecated use {@link #isGeneratorOwner(String, int)} instead.
     *//*
    public static boolean isAGenerator(String nickname, int generatorId) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();
        Generator generator = Main.db.getPlayerGeneratorsById(generatorId);
        // generator is null
        if (generator == null) {
            if (isDebugEnabled) {
                debug.addLine("Method: isAGenerator");
                debug.addLine("Isn't a Generator - Generator id is invalid, the return is null");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return false;
        }
        ArrayList<Generator> generators = Main.db.getPlayerGeneratorsByName(nickname);
        for (Generator value : generators) {
            if (value.getId() == generator.getId()) {
                if (isDebugEnabled) {
                    debug.addLine("Method: isAGenerator");
                    debug.addLine("Is a Generator");
                    debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                    debug.debug("API");
                }
                return true;
            }
        }
        if (isDebugEnabled) {
            debug.addLine("Method: isAGenerator");
            debug.addLine("Isn't a Generator");
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug("API");
        }
        return false;
    }
*/
    /**
     * Check if the provided block location is a Frame of the CubeGenerator.
     *
     * @param blockLocation location of the frame block.
     * @return if the provided block location is a Frame of the CubeGenerator.
     *//*
    public static boolean getFrameBlockAtLocation(Location blockLocation) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();
        Coordinate coordinate = Main.db.checkIfBlockIsAGeneratorBlock(blockLocation, "PLACED", "FRAME");
        if (isDebugEnabled) {
            debug.addLine("Method: getFrameBlockAtLocation");
            debug.addLine("Coordinate is: " + coordinate);
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug("API");
        }
        // controllo se è null
        return coordinate != null;
    }
*/
    /**
     * Returns the custom drop of the provided block or NULL if the generatorId is invalid.
     *
     * @param brokenBlock broken block as ItemStack.
     * @param generatorId id of the CubeGenerator.
     * @param pickaxe     pickaxe as ItemStack (you can pass anything, but is suggested a pickaxe with enchantments).
     *                    <br/>If is null, it will be converted to DIAMOND_PICKAXE.
     * @return the drop as ItemStack of the provided block.<br/> NULL if the generatorId is invalid.
     *//*
    public static ItemStack getConvertedBlock(ItemStack brokenBlock, int generatorId, ItemStack pickaxe) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();
        if (pickaxe == null) {
            pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        }

        Generator generator = Main.db.getPlayerGeneratorsById(generatorId);
        if (generator == null) {
            if (isDebugEnabled) {
                debug.addLine("Method: getConvertedBlock");
                debug.addLine("Generator is null");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return null;
        }

        // REDSTONE_ORE material correction
        Material mat = brokenBlock.getType();
        if (mat.toString().toUpperCase().contains("REDSTONE_ORE")) {
            if (mat.toString().toUpperCase().contains("DEEPSLATE"))
                mat = Material.DEEPSLATE_REDSTONE_ORE;
            else
                mat = Material.REDSTONE_ORE;

        }

        // controllo se il piccone ha silk touch
        if (pickaxe.containsEnchantment(Enchantment.SILK_TOUCH)) {
            if (Main.instance.getGeneratorsGestion().getLevels().get(generator.getLevel()).getMaterialsSilk().get(mat + "") != null) {
                String material = Main.instance.getGeneratorsGestion().getLevels().get(generator.getLevel()).getMaterialsSilk().get(mat + "");
                if (isDebugEnabled) {
                    debug.addLine("Method: getConvertedBlock");
                    debug.addLine("Silktouch active");
                    debug.addLine("Material is: " + material);
                    debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                    debug.debug("API");
                }
                // controllo se contiene il ;
                if (material.contains(";")) {
                    // splitto
                    String[] split = material.split(";");
                    // ritorno l'item con lo short
                    return new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1]));
                } else {
                    // ritorno l'item senza short
                    return new ItemStack(Material.getMaterial(material), 1, (short) 0);
                }
            }
        } else {
            if (Main.instance.getGeneratorsGestion().getLevels().get(generator.getLevel()).getMaterialsWithoutSilk().get(mat + "") != null) {
                String material = Main.instance.getGeneratorsGestion().getLevels().get(generator.getLevel()).getMaterialsWithoutSilk().get(mat + "");
                if (isDebugEnabled) {
                    debug.addLine("Method: getConvertedBlock");
                    debug.addLine("Silktouch disabled");
                    debug.addLine("Material is: " + material);
                    debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                    debug.debug("API");
                }
                // controllo se contiene il ;
                if (material.contains(";")) {
                    // splitto
                    String[] split = material.split(";");
                    // ritorno l'item con lo short
                    return new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1]));
                } else {
                    // ritorno l'item senza short
                    return new ItemStack(Material.getMaterial(material), 1, (short) 0);
                }
            }
        }
        return brokenBlock;
    }
*/
    /**
     * Add ItemStack to:
     * <br/>- CubeGenerator Inventory.
     * <br/>- Player Inventory (Player Required).
     * <br/>- Drop on ground (Player Required).
     * <br/>This method depends on the configuration ("ItemCollecting").
     *
     * @param item        ItemStack to add.
     * @param generatorId id of the CubeGenerator.
     * @return if the ItemStack has been added to the Inventory of the CubeGenerator.
     *//*
    public static boolean addBlockToInventory(ItemStack item, int generatorId) {
        return addBlockToInventory(null, item, generatorId);
    }
*/
    /**
     * Add ItemStack to:
     * <br/>- CubeGenerator Inventory.
     * <br/>- Player Inventory (Player Required).
     * <br/>- Drop on ground (Player Required).
     * <br/>This method depends on the configuration ("ItemCollecting").
     *
     * @param player      player execution action
     * @param item        ItemStack to add.
     * @param generatorId id of the CubeGenerator.
     * @return if the ItemStack has been added to the Inventory of the CubeGenerator.
     *//*
    public static boolean addBlockToInventory(Player player, ItemStack item, int generatorId) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();

        if (item == null || item.getType().equals(Material.AIR)) {
            if (isDebugEnabled) {
                debug.addLine("Method: addBlockToInventory");
                debug.addLine("Material is AIR");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return false;
        }

        // conto gli item nel db
        int count = Main.AddMaterialManager.countItemsInAGeneratorWithIdGenerator(generatorId);
        if (count >= Main.instance.getConfigGestion().getInterfaces().get("Generator").getSizeModificableSlot()) {
            if (isDebugEnabled) {
                debug.addLine("Method: addBlockToInventory");
                debug.addLine("Slots full");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return false;
        }
        Main.AddMaterialManager.addMaterial(player, item.getType().toString(), item.getDurability(), item.getAmount(), generatorId);
        Main.instance.getConfigGestion().getInterfaces().get("Generator").refreshItemInInventory(player != null ? player.getName() : null, Main.db.getPlayerGeneratorsById(generatorId), Main.AddMaterialManager.getItemsInGeneratorWithId(generatorId));
        if (isDebugEnabled) {
            debug.addLine("Method: addBlockToInventory");
            debug.addLine("Added: " + item.getType());
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug("API");
        }
        return true;
    }
*/
    /**
     * Change the provided location to a random block of the CubeGenerator.
     *
     * @param generatorId   id of the CubeGenerator
     * @param blockLocation location of the block
     *//*
    public static void setRandomGeneratorBlock(int generatorId, Location blockLocation) {
        setRandomGeneratorBlock(generatorId, blockLocation, true, -1, true);
    }
*/
    /**
     * Change the provided location to a random block of the CubeGenerator.
     *
     * @param generatorId              id of the CubeGenerator
     * @param blockLocation            location of the block
     * @param setAirBefore             if true, the block will be set to air before changing it (default true)
     * @param customRespawnDelay       custom respawn delay in milliseconds (set to -1 to use the default of the generator)
     * @param preventPlayerSuffocation <b>only for server version 1.13+</b> | if true, check if the player can suffocate before changing the block (default true)
     *//*
    public static void setRandomGeneratorBlock(int generatorId, Location blockLocation, boolean setAirBefore, long customRespawnDelay, boolean preventPlayerSuffocation) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();

        Generator generator = Main.db.getPlayerGeneratorsById(generatorId);
        if (generator == null) {
            if (isDebugEnabled) {
                debug.addLine("Method: setRandomGeneratorBlock");
                debug.addLine("Generator is null");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return;
        }
        final Block block = blockLocation.getBlock();
        if (block.getType().equals(Material.AIR)) {
            if (isDebugEnabled) {
                debug.addLine("Method: setRandomGeneratorBlock");
                debug.addLine("Block is AIR");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return;
        }

        if (setAirBefore) {
            if (Bukkit.isPrimaryThread()) {
                block.setType(Material.AIR);
            } else {
                Bukkit.getScheduler().runTask(Main.instance, () -> block.setType(Material.AIR));
            }
        }

        long _timeRespawn;
        // check if customRespawnDelay is -1, if it is, use the default of the generator
        if (customRespawnDelay != -1) {
            // Check if custom upgrade is enabled
            if (!Main.instance.getConfigGestion().getCubes().get(generator.getSize()).isCustomUpgrade()) {
                _timeRespawn = Main.instance.getGeneratorsGestion().getLevels().get(generator.getLevel()).getRespawnDelay();
            } else {
                final String[] customUpgradeSplitting = generator.getCustomUpgrade().split("/");
                _timeRespawn = Main.instance.getCustomUpgradeGestion().getCustomUpgrade().get(generator.getSize()).getRespawnDelay().get(Integer.parseInt(customUpgradeSplitting[0].split("=")[1])).getTime();
            }
        } else {
            _timeRespawn = customRespawnDelay;
        }
        final long timeRespawn = _timeRespawn;

        new BukkitRunnable() {

            @Override
            public void run() {
                if (preventPlayerSuffocation) { // only for 1.13+
                    try {
                        // check player collision
                        BoundingBox box = block.getBoundingBox();
                        box.resize(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 2,
                                block.getZ() + 1);
                        // check entity in box
                        Collection<Entity> ListEntity = block.getWorld().getNearbyEntities(box);
                        for (Entity entity : ListEntity) {// while entities
                            if (entity.getType().equals(EntityType.PLAYER)) {// is a player?
                                if (isDebugEnabled) {
                                    debug.addLine("Method: setRandomGeneratorBlock");
                                    debug.addLine("Player inside Generator");
                                    debug.addLine("Block is:" + block);
                                    debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                                    debug.debug("API");
                                }
                                // try again
                                return;
                            }
                        }
                    } catch (NoSuchMethodError ignored) {
                        // Ignored server version < 1.13
                    }
                }

                block.setType(Randomize.getRandomBlock(generator.getLevel()).getType());
                this.cancel();
            }
        }.runTaskTimer(Main.instance, timeRespawn, timeRespawn);
    }
*/
    /**
     * Returns ArrayList<Location> of all the blocks that can be broken of the provided CubeGenerator.
     *
     * @param generatorId id of the CubeGenerator.
     * @return Locations of all the blocks that can be broken of the provided CubeGenerator.
     *//*
    public static ArrayList<Location> getBreakableLocationsByGenerator(int generatorId) {
        return getCoordinatesFromId(generatorId);
    }*/

    /**
     * Returns ArrayList<Location> of all the blocks that can be broken of the provided CubeGenerator.
     *
     * @param generatorId id of the CubeGenerator.
     * @return Locations of all the blocks that can be broken of the provided CubeGenerator.
     * @deprecated use {@link #getBreakableLocationsByGenerator(int)} instead.
     *//*
    public static ArrayList<Location> getCoordinatesFromId(int generatorId) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();

        Generator generator = Main.db.getPlayerGeneratorsById(generatorId);
        if (generator == null) {
            if (isDebugEnabled) {
                debug.addLine("Method: getCoordinatesFromId");
                debug.addLine("Generator is null");
                debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
                debug.debug("API");
            }
            return new ArrayList<Location>();
        }

        String[] locationsSplit = generator.getLocations().split(";");
        ArrayList<Location> locations = new ArrayList<Location>();
        for (String s : locationsSplit) {
            String[] location = s.split(":");
            if (location[4].equalsIgnoreCase("NULL")) {
                Location loc = new Location(Bukkit.getWorld(location[3]), Integer.parseInt(location[0]), Integer.parseInt(location[1]), Integer.parseInt(location[2]));
                locations.add(loc);
            }
        }
        if (isDebugEnabled) {
            debug.addLine("Method: getCoordinatesFromId");
            debug.addLine("Generator:" + generator);
            debug.addLine("Locations:" + locations);
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug("API");
        }
        return locations;
    }*/

    /**
     * Returns true if the event is completed successfully, false if something goes wrong.
     * <br/>
     * <i>So if <b>true</b> the block is broken, if <b>false</b> the block is not broken.</i>
     * <br/>
     * <b>NB:</b> This method requires the player online!
     *
     * @param block      The block that is broken
     * @param itemStack  The item used to break the block
     * @param playerName The name of the player that broke the block
     * @return true if the event is completed successfully, false if something goes wrong.
     *//*
    public static boolean doBlockBreak(Block block, ItemStack itemStack, String playerName) {
        return Main.instance.BlockBreak.executeBlockBreak(block, itemStack, playerName);
    }*/

}
