package xyz.elevated.frequency.check.impl.fly;

import org.bukkit.Location;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.update.PositionUpdate;

@CheckData(name = "Fly (B)")
public final class FlyB extends PositionCheck {

  private double lastDeltaY, buffer;
  private int ticks;

  public FlyB(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(PositionUpdate positionUpdate) {
    Location from = positionUpdate.getFrom();
    Location to = positionUpdate.getTo();

    double deltaY = to.getY() - from.getY();
    double estimation = (lastDeltaY - 0.08) * 0.9800000190734863;

    boolean resetting = Math.abs(deltaY) + 0.0980000019 < 0.05;
    boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.VELOCITY);

    boolean touchingAir = playerData.getPositionManager().getTouchingAir().get();

    if (exempt || resetting) return;

    if (touchingAir) {
      ++ticks;

      if (ticks > 5 && Math.abs(estimation - deltaY) > 0.01) {
        buffer += 1.5;

        if (buffer > 5) fail();
      } else {
        buffer = Math.max(0, buffer - 1.25);
      }
    } else {
      ticks = 0;
    }

    lastDeltaY = deltaY;
  }
}
