package xyz.elevated.frequency.check.impl.killaura;

import com.google.common.collect.Lists;
import java.util.Deque;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.RotationCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.update.RotationUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "KillAura (B)")
public final class KillAuraB extends RotationCheck {

  private final Deque<Float> samplesYaw = Lists.newLinkedList();
  private final Deque<Float> samplesPitch = Lists.newLinkedList();

  private double buffer, lastAverage;

  public KillAuraB(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(RotationUpdate rotationUpdate) {
    float deltaYaw = rotationUpdate.getDeltaYaw();
    float deltaPitch = rotationUpdate.getDeltaPitch();

    boolean attacking =
        System.currentTimeMillis() - playerData.getActionManager().getLastAttack() < 500L;

    if (deltaYaw > 0.0 && deltaPitch > 0.0 && attacking) {
      samplesYaw.add(deltaYaw);
      samplesPitch.add(deltaPitch);
    }

    if (samplesPitch.size() == 20 && samplesYaw.size() == 20) {
      double averageYaw = samplesYaw.stream().mapToDouble(d -> d).average().orElse(0.0);
      double averagePitch = samplesPitch.stream().mapToDouble(d -> d).average().orElse(0.0);

      double deviation = MathUtil.getStandardDeviation(samplesPitch);
      double averageDelta = Math.abs(averagePitch - lastAverage);

      if (deviation > 6.f && averageDelta > 1.5f && averageYaw < 30.d) {
        buffer += 0.5;

        if (buffer > 2.0) fail();
      } else {
        buffer = Math.max(buffer - 0.125, 0);
      }

      samplesYaw.clear();
      samplesPitch.clear();
      lastAverage = averagePitch;
    }
  }
}
