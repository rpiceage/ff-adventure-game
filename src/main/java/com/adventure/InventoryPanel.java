package com.adventure;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InventoryPanel extends JPanel {
    private final Hero hero;
    private final GameController controller;
    private final ChapterStateManager chapterState;
    private final Runnable onItemUsed;
    private final Runnable onStatsUpdate;
    private final NotificationManager notificationManager;
    
    public InventoryPanel(Hero hero, GameController controller, ChapterStateManager chapterState, 
                         Runnable onItemUsed, Runnable onStatsUpdate, NotificationManager notificationManager) {
        this.hero = hero;
        this.controller = controller;
        this.chapterState = chapterState;
        this.onItemUsed = onItemUsed;
        this.onStatsUpdate = onStatsUpdate;
        this.notificationManager = notificationManager;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(UIConstants.SEMI_TRANSPARENT_BLACK);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
    }
    
    public void updateItems() {
        removeAll();
        
        Map<String, Integer> useItemMap = getUseItemMap();
        com.adventure.actions.SellItemAction sellItemAction = getSellItemAction();
        
        for (Item item : hero.getInventory()) {
            JButton itemButton = new JButton(item.getName());
            itemButton.setFont(UIConstants.FONT_SMALL);
            itemButton.setForeground(Color.WHITE);
            itemButton.setBackground(UIConstants.SEMI_TRANSPARENT_BLACK);
            itemButton.setOpaque(false);
            itemButton.setContentAreaFilled(false);
            itemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            if (useItemMap.containsKey(item.getName())) {
                int targetChapter = useItemMap.get(item.getName());
                itemButton.addActionListener(e -> {
                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_USED_ITEM), item.getName(), targetChapter));
                    controller.goToChapter(targetChapter);
                    onItemUsed.run();
                });
            } else if (sellItemAction != null) {
                Map<String, Object> sellActionData = getSellItemActionData();
                int goldPerItem = sellItemAction.getGoldPerItem(sellActionData);
                int maxItemCount = sellItemAction.getMaxItemCount(sellActionData);
                
                if (chapterState.getSoldItemsCount() < maxItemCount) {
                    itemButton.addActionListener(e -> {
                        hero.modifyGold(goldPerItem);
                        hero.removeItem(item);
                        chapterState.incrementSoldItems();
                        controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_SOLD_ITEM), item.getName(), goldPerItem));
                        onStatsUpdate.run();
                        updateItems();
                    });
                } else {
                    itemButton.setEnabled(false);
                }
            } else if (item.canUseAnyTime() && item.hasEffect()) {
                // Potion or special item that can be used anytime
                itemButton.addActionListener(e -> {
                    // Check if battle is active
                    if (controller.isBattleActive()) {
                        return; // Silently ignore during battle
                    }
                    
                    item.use(hero);
                    hero.removeItem(item);
                    
                    String attributeName = "";
                    if (item.getName().contains(Messages.get(Messages.Key.POTION_SKILL))) {
                        attributeName = Messages.get(Messages.Key.SKILL);
                    } else if (item.getName().contains(Messages.get(Messages.Key.POTION_STAMINA))) {
                        attributeName = Messages.get(Messages.Key.STAMINA);
                    } else if (item.getName().contains(Messages.get(Messages.Key.POTION_LUCK))) {
                        attributeName = Messages.get(Messages.Key.LUCK);
                    }
                    
                    notificationManager.show(String.format(Messages.get(Messages.Key.POTION_USED), attributeName));
                    onStatsUpdate.run();
                    updateItems();
                });
            } else {
                itemButton.addActionListener(e -> showItemCantUsePopup());
            }
            
            add(itemButton);
        }
        
        revalidate();
        repaint();
    }
    
    private Map<String, Integer> getUseItemMap() {
        Map<String, Integer> map = new java.util.HashMap<>();
        for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
            if (actionData.containsKey("useItem")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) actionData.get("useItem");
                for (Map<String, Object> itemData : items) {
                    String itemName = (String) itemData.get("item");
                    int chapter = (Integer) itemData.get("chapter");
                    map.put(itemName, chapter);
                }
            }
        }
        return map;
    }
    
    private com.adventure.actions.SellItemAction getSellItemAction() {
        for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
            if (actionData.containsKey("sellItem")) {
                return new com.adventure.actions.SellItemAction();
            }
        }
        return null;
    }
    
    private Map<String, Object> getSellItemActionData() {
        for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
            if (actionData.containsKey("sellItem")) {
                return actionData;
            }
        }
        return null;
    }
    
    private void showItemCantUsePopup() {
        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), 
            Messages.get(Messages.Key.ITEM_CANT_USE), 
            Messages.get(Messages.Key.ITEMS_TITLE), 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
