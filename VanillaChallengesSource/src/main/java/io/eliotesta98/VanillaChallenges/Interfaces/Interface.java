package io.eliotesta98.VanillaChallenges.Interfaces;

import com.HeroxWar.HeroxCore.SoundGesture.SoundType;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interface {

    private final String nameInterface, nameInterfaceToOpen, nameInterfaceToReturn;
    private String title;
    private List<String> slots;
    private final Map<String, ItemConfig> itemsConfig;
    private boolean debug;
    private final int sizeModificableSlot;
    private final Map<String, Inventory> interfacesOpened = new HashMap<>();
    private final SoundType soundOpen;

    public Interface(String title, String soundOpen, List<String> slots, Map<String, ItemConfig> itemsConfig, boolean debug, int sizeModificableSlot, String nameInterface, String nameInterfaceToOpen, String nameInterfaceToReturn) {
        this.title = title;
        this.soundOpen = new SoundType(soundOpen, 50, 1.0);
        this.itemsConfig = itemsConfig;
        this.debug = debug;
        this.sizeModificableSlot = sizeModificableSlot;
        this.slots = slots;
        this.nameInterface = nameInterface;
        this.nameInterfaceToOpen = nameInterfaceToOpen;
        this.nameInterfaceToReturn = nameInterfaceToReturn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<String> slots) {
        this.slots = slots;
    }

    public Map<String, ItemConfig> getItemsConfig() {
        return itemsConfig;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getSizeModificableSlot() {
        return sizeModificableSlot;
    }

    public String getNameInterfaceToOpen() {
        return nameInterfaceToOpen;
    }

    public String getNameInterfaceToReturn() {
        return nameInterfaceToReturn;
    }

    public void removeInventory(String playerName) {
        interfacesOpened.remove(playerName);
    }

    public void closeAllInventories() {
        for (Map.Entry<String, Inventory> inventory : interfacesOpened.entrySet()) {
            if (Bukkit.getPlayer(inventory.getKey()) == null) {
                continue;
            }
            Bukkit.getPlayer(inventory.getKey()).closeInventory();
        }
    }

    //Challenges interface
    public void openInterface(ArrayList<?> items, Player p, int numberOfPage) {
        DebugUtils debug = new DebugUtils("Interface Creation");
        long tempo = System.currentTimeMillis();
        VanillaChallengesInterfaceHolder holder = new VanillaChallengesInterfaceHolder(slots.size(),
                ChatColor.translateAlternateColorCodes('&', title));
        // prendo l'inventario
        final Inventory inventory = holder.getInventory();
        int slotModificable = items.size() - sizeModificableSlot;
        interfacesOpened.put(p.getName(), inventory);
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            boolean lock = Main.instance.getConfigGesture().isLockedInterface();
            int countItems = 0;
            for (int i = 0; i < slots.size(); i++) {// scorro gli slot
                String slot = slots.get(i);// prendo lo slot
                if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Challenge")) {
                    if (items.size() > countItems) {
                        if (items.get(countItems) instanceof Challenge) {
                            Challenge challenge = (Challenge) items.get(countItems);
                            Challenge challengeComplete = Main.instance.getConfigGesture().getChallenges().get(challenge.getChallengeName());
                            if (challenge.getChallengeName().contains("Event_")) {
                                challengeComplete = Main.instance.getConfigGesture().getChallengesEvent().get(challenge.getChallengeName().replace("Event_", ""));
                            }
                            if (!lock) {
                                inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nameInterface, numberOfPage,
                                        "vc.numberPage:" + (numberOfPage) +
                                                ";vc.challengeName:" + challenge.getChallengeName() +
                                                ";vc.challengeTime:" + challenge.getTimeChallenge() +
                                                ";vc.challengeDescription:" + challengeComplete.getTitle() +
                                                ";vc.challengePoint:" + challengeComplete.getPoint() +
                                                ";vc.challengeItemsInHand:" + challengeComplete.getItemsInHand() +
                                                ";vc.challengeWords:" + challengeComplete.getWorlds() +
                                                ";vc.challengeBlocks:" + challengeComplete.getBlocks() +
                                                ";vc.challengeRewards:" + challengeComplete.getRewards() +
                                                ";vc.challengeSneaking:" + challengeComplete.getSneaking() +
                                                ";vc.challengeBlocksOnPlace:" + challengeComplete.getBlocksOnPlace() +
                                                ";vc.challengeVehicles:" + challengeComplete.getVehicle() +
                                                ";vc.challengeMobs:" + challengeComplete.getMobs() +
                                                ";vc.challengeItems:" + challengeComplete.getItems() +
                                                ";vc.challengeCauses:" + challengeComplete.getCauses() +
                                                ";vc.challengeColors:" + challengeComplete.getColors() +
                                                ";vc.challengeItem:" + challengeComplete.getItemChallenge()
                                        , i));
                            } else {
                                if (Main.instance.getDailyChallenge().getChallengeName().equalsIgnoreCase(challenge.getChallengeName())) {
                                    inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nameInterface, numberOfPage,
                                            "vc.numberPage:" + (numberOfPage) +
                                                    ";vc.challengeName:" + challenge.getChallengeName() +
                                                    ";vc.challengeTime:" + challenge.getTimeChallenge() +
                                                    ";vc.challengeDescription:" + challengeComplete.getTitle() +
                                                    ";vc.challengePoint:" + challengeComplete.getPoint() +
                                                    ";vc.challengeItemsInHand:" + challengeComplete.getItemsInHand() +
                                                    ";vc.challengeWords:" + challengeComplete.getWorlds() +
                                                    ";vc.challengeBlocks:" + challengeComplete.getBlocks() +
                                                    ";vc.challengeRewards:" + challengeComplete.getRewards() +
                                                    ";vc.challengeSneaking:" + challengeComplete.getSneaking() +
                                                    ";vc.challengeBlocksOnPlace:" + challengeComplete.getBlocksOnPlace() +
                                                    ";vc.challengeVehicles:" + challengeComplete.getVehicle() +
                                                    ";vc.challengeMobs:" + challengeComplete.getMobs() +
                                                    ";vc.challengeItems:" + challengeComplete.getItems() +
                                                    ";vc.challengeCauses:" + challengeComplete.getCauses() +
                                                    ";vc.challengeColors:" + challengeComplete.getColors() +
                                                    ";vc.challengeItem:" + challengeComplete.getItemChallenge()
                                            , i));
                                } else {
                                    inventory.setItem(i, itemsConfig.get(slot.concat("" + 1)).createItemConfig(nameInterface, numberOfPage,
                                            "vc.numberPage:" + (numberOfPage) +
                                                    ";vc.challengeName:" + challenge.getChallengeName() +
                                                    ";vc.challengeTime:" + challenge.getTimeChallenge() +
                                                    ";vc.challengeDescription:" + challengeComplete.getTitle() +
                                                    ";vc.challengePoint:" + challengeComplete.getPoint() +
                                                    ";vc.challengeItemsInHand:" + challengeComplete.getItemsInHand() +
                                                    ";vc.challengeWords:" + challengeComplete.getWorlds() +
                                                    ";vc.challengeBlocks:" + challengeComplete.getBlocks() +
                                                    ";vc.challengeRewards:" + challengeComplete.getRewards() +
                                                    ";vc.challengeSneaking:" + challengeComplete.getSneaking() +
                                                    ";vc.challengeBlocksOnPlace:" + challengeComplete.getBlocksOnPlace() +
                                                    ";vc.challengeVehicles:" + challengeComplete.getVehicle() +
                                                    ";vc.challengeMobs:" + challengeComplete.getMobs() +
                                                    ";vc.challengeItems:" + challengeComplete.getItems() +
                                                    ";vc.challengeCauses:" + challengeComplete.getCauses() +
                                                    ";vc.challengeColors:" + challengeComplete.getColors() +
                                                    ";vc.challengeItem:" + challengeComplete.getItemChallenge()
                                            , i));
                                }
                            }
                            countItems++;
                        }
                    }
                } else if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("LeftPage")) {// se è una morte
                    if (numberOfPage - 1 != 0) {
                        inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nameInterface, numberOfPage - 1, "vc.numberPage:" + (numberOfPage - 1), i));
                    } else {
                        for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                            if (itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Border")) {
                                inventory.setItem(i, itemsConfig.get(itemConfig.getKey()).createItemConfig(nameInterface, 0, "", i));
                                break;
                            }
                        }
                    }
                } else if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("RightPage")) {// se è una morte
                    if (slotModificable == 0) {
                        inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nameInterface, numberOfPage + 1, "vc.numberPage:" + (numberOfPage + 1), i));
                    } else {
                        for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                            if (itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Border")) {
                                inventory.setItem(i, itemsConfig.get(itemConfig.getKey()).createItemConfig(nameInterface, 0, "", i));
                                break;
                            }
                        }
                    }
                } else {
                    inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nameInterface, 0, "", i));
                }
            }
            soundOpen.playSound(p);
        });
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> p.openInventory(inventory));
        if (this.debug) {
            debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
            debug.debug();
        }
    }

}
