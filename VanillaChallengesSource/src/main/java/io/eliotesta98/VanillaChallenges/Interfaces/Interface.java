package io.eliotesta98.VanillaChallenges.Interfaces;

import com.HeroxWar.HeroxCore.SoundGesture.SoundType;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
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
    private final Map<String, ExtendedInventory> inventoriesOpened = new HashMap<>();
    private final SoundType soundOpen;

    private boolean lockableInterface = false;

    public Interface(String title, String soundOpen, List<String> slots, Map<String, ItemConfig> itemsConfig,
                     boolean debug, int sizeModificableSlot, String nameInterface,
                     String nameInterfaceToOpen, String nameInterfaceToReturn, boolean lockableInterface) {
        this.title = title;
        this.soundOpen = new SoundType(soundOpen, 50, 1.0);
        this.itemsConfig = itemsConfig;
        this.debug = debug;
        this.sizeModificableSlot = sizeModificableSlot;
        this.slots = slots;
        this.nameInterface = nameInterface;
        this.nameInterfaceToOpen = nameInterfaceToOpen;
        this.nameInterfaceToReturn = nameInterfaceToReturn;
        this.lockableInterface = lockableInterface;
        refreshInterface();
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
        inventoriesOpened.remove(playerName);
    }

    public void closeAllInventories() {
        for (String player : inventoriesOpened.keySet()) {
            Player playerInstance = Bukkit.getPlayer(player);
            if (playerInstance == null) {
                continue;
            }
            playerInstance.closeInventory();
        }
    }

    //Challenges interface
    public void openInterface(List<?> items, Player p, int numberOfPage) {
        DebugUtils debug = new DebugUtils("Interface Creation");
        long tempo = System.currentTimeMillis();
        VanillaChallengesInterfaceHolder holder = new VanillaChallengesInterfaceHolder(slots.size(),
                Main.messageGesturePaper.applyColorLegacy(title));
        // prendo l'inventario
        final Inventory inventory = holder.getInventory();
        int slotModificable = items.size() - sizeModificableSlot;
        inventoriesOpened.put(p.getName(), new ExtendedInventory(inventory, p, numberOfPage, this, items));
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            int countItems = 0;
            for (int i = 0; i < slots.size(); i++) {// for every slot
                String slot = slots.get(i);// get the current slot
                NbtList nbtListBorder = new NbtList("vc.currentInterface=" + nameInterface + ";vc.positionItem=" + i); // create the nbt list for borders
                if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Challenge")) {
                    if (items.size() > countItems) {
                        if (items.get(countItems) instanceof Challenge) {
                            Challenge challenge = ((Challenge) items.get(countItems));
                            Challenge challengeComplete = Main.instance.getConfigGestion().getChallenges().get(challenge.getChallengeName());
                            if (challenge.getChallengeName().contains("Event_")) {
                                challengeComplete = Main.instance.getConfigGestion().getChallengesEvent().get(challenge.getChallengeName().replace("Event_", ""));
                            }
                            challengeComplete.setDate(challenge.getDate());
                            if(challenge.getChallengeName().equalsIgnoreCase(Main.instance.getDailyChallenge().getChallengeName())) {
                                challengeComplete.setTimeChallenge(Main.instance.getDailyChallenge().getTimeChallenge());
                            }
                            String challengeNbts = challengeComplete.encodeNbts();
                            NbtList nbtList = new NbtList("vc.numberPage=" + (numberOfPage) +
                                    ";" + challengeNbts +
                                    ";vc.positionItem=" + i +
                                    ";vc.currentInterface=" + nameInterface
                            );
                            if (!lockableInterface) {
                                inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nbtList));
                            } else {
                                if (Main.instance.getDailyChallenge().getChallengeName().equalsIgnoreCase(challenge.getChallengeName())) {
                                    inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nbtList));
                                } else {
                                    inventory.setItem(i, itemsConfig.get(slot.concat("1")).createItemConfig(nbtList));
                                }
                            }
                            countItems++;
                        }
                    }
                } else if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("LeftPage")) {
                    if (numberOfPage - 1 != 0) {
                        NbtList nbtList = new NbtList("vc.numberPage=" + (numberOfPage - 1) + ";vc.currentInterface=" + nameInterface + ";vc.positionItem=" + i);
                        inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nbtList));
                    } else {
                        for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                            if (itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Border")) {
                                inventory.setItem(i, itemsConfig.get(itemConfig.getKey()).createItemConfig(nbtListBorder));
                                break;
                            }
                        }
                    }
                } else if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("RightPage")) {
                    if (slotModificable == 0) {
                        NbtList nbtList = new NbtList("vc.numberPage=" + (numberOfPage + 1) + ";vc.currentInterface=" + nameInterface + ";vc.positionItem=" + i);
                        inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nbtList));
                    } else {
                        for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                            if (itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Border")) {
                                inventory.setItem(i, itemsConfig.get(itemConfig.getKey()).createItemConfig(nbtListBorder));
                                break;
                            }
                        }
                    }
                } else {
                    inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nbtListBorder));
                }
            }
            soundOpen.playSound(p);
        });
        Bukkit.getScheduler().runTask(Main.instance, () -> p.openInventory(inventory));
        if (this.debug) {
            debug.addLine("execution time= " + (System.currentTimeMillis() - tempo));
            debug.debug();
        }
    }

    public void refreshInterface() {
        Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {
            for (Map.Entry<String, ExtendedInventory> inventory : inventoriesOpened.entrySet()) {
                refreshSlots(inventory.getValue());
            }
        }, 0, 100L);
    }

    public void refreshSlots(ExtendedInventory extendedInventory) {
        for (int i = 0; i < slots.size(); i++) {
            String slot = slots.get(i);
            if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Challenge")) {
                // Refresh only the first slot of page 1
                if (extendedInventory.getItems().get(0) instanceof Challenge && extendedInventory.getPage() == 1) {
                    Challenge challenge = (Challenge) extendedInventory.getItems().get(0);
                    Challenge challengeComplete = Main.instance.getConfigGestion().getChallenges().get(challenge.getChallengeName());
                    if (challenge.getChallengeName().contains("Event_")) {
                        challengeComplete = Main.instance.getConfigGestion().getChallengesEvent().get(challenge.getChallengeName().replace("Event_", ""));
                    }
                    if(challenge.getChallengeName().equalsIgnoreCase(Main.instance.getDailyChallenge().getChallengeName())) {
                        challengeComplete.setTimeChallenge(Main.instance.getDailyChallenge().getTimeChallenge());
                    }
                    String challengeNbts = challengeComplete.encodeNbts();
                    NbtList nbtList = new NbtList("vc.numberPage=" + (extendedInventory.getPage()) +
                            ";" + challengeNbts +
                            ";vc.positionItem=" + i +
                            ";vc.currentInterface=" + nameInterface
                    );
                    if (!lockableInterface) {
                        extendedInventory.getInventory().setItem(i, itemsConfig.get(slot).createItemConfig(nbtList));
                    } else {
                        if (Main.instance.getDailyChallenge().getChallengeName().equalsIgnoreCase(challenge.getChallengeName())) {
                            extendedInventory.getInventory().setItem(i, itemsConfig.get(slot).createItemConfig(nbtList));
                        } else {
                            extendedInventory.getInventory().setItem(i, itemsConfig.get(slot.concat("1")).createItemConfig(nbtList));
                        }
                    }
                }
                break;
            }
        }
    }

}
