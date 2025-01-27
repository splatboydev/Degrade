package xyz.elevated.frequency.alert;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.elevated.frequency.Frequency;
import xyz.elevated.frequency.check.Check;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.util.ColorUtil;

import java.util.List;

@RequiredArgsConstructor
public final class AlertManager {
    private final Check<?> check;

    private final String base = ColorUtil.format("&8[&7Degrade&8] &a%s &7failed &a%s &8[&7VL&A%s&8]");
    private final String broadcast = ColorUtil.format("&8[&7Degrade&8] &a%s &7was found using an unfair advantage and was removed from the network.");

    private final List<Long> alerts = Lists.newArrayList();

    public void fail() {
        long now = System.currentTimeMillis();

        PlayerData playerData = check.getPlayerData();
        Player player = playerData.getBukkitPlayer();

        if (alerts.contains(now)) {
            return;
        }

        alerts.add(now);

        int violations = (int) (alerts.stream().filter(violation -> violation + 9000L > System.currentTimeMillis()).count());
        int threshold = check.getThreshold();

        String alert = String.format(base, player.getName(), check.getCheckName(), violations);
        String message = String.format(broadcast, player.getName());

        if (violations > threshold) {
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " [Frequency] Unfair Advantage");;
            Bukkit.broadcastMessage(message);

            alerts.clear();
        }

        // Execute the alert on a separate thread as we need to loop
        Frequency.INSTANCE.getExecutorAlert().execute(() -> Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(send -> send.hasPermission("degrade.alerts"))
                        .forEach(send -> send.sendMessage(alert)));
    }
}
