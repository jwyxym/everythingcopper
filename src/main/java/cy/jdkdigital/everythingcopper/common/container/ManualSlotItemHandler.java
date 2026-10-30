package cy.jdkdigital.everythingcopper.common.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

import org.jetbrains.annotations.NotNull;

public class ManualSlotItemHandler extends SlotItemHandler
{
    ManualItemHandler handler;

    public ManualSlotItemHandler(ManualItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        handler = itemHandler;
    }

    @Override
	public boolean mayPlace(@NotNull ItemStack stack) {
        return handler.isItemValid(this.getSlotIndex(), stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !this.handler.extractItem(this.getSlotIndex(), 1, true, false).isEmpty();
    }

    @Override
	@NotNull
    public ItemStack remove(int amount) {
        return this.handler.extractItem(this.getSlotIndex(), amount, false, false);
    }
}
