package fnaf.ui;

import fnaf.render.ArtGenerator;

import java.awt.*;

/**
 * Paycheck Screen awarded after beating Night 5, Night 6, or Custom Night.
 */
public class PaycheckScreen {
    private int night = 5;

    public void start(int night) {
        this.night = night;
    }

    public void render(Graphics2D g, int W, int H) {
        g.setColor(new Color(10, 10, 14));
        g.fillRect(0, 0, W, H);

        if (night == 7) {
            // Notice of Termination (Pink Slip)
            drawPinkSlip(g, W, H);
        } else {
            // Bank Check
            drawCheck(g, W, H, night == 6);
        }

        // Bottom prompt
        if (System.currentTimeMillis() % 1200 < 800) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            String prompt = "[ CLICK ANYWHERE TO CONTINUE ]";
            g.drawString(prompt, (W - fm.stringWidth(prompt)) / 2, H - 40);
        }

        ArtGenerator.drawCRTScanlines(g, W, H);
        ArtGenerator.drawStaticNoise(g, W, H, 0.05f);
    }

    private void drawCheck(Graphics2D g, int W, int H, boolean isOvertime) {
        int cw = 760;
        int ch = 380;
        int cx = (W - cw) / 2;
        int cy = (H - ch) / 2;

        // Check background
        g.setColor(new Color(225, 235, 220));
        g.fillRect(cx, cy, cw, ch);
        g.setColor(new Color(50, 70, 50));
        g.setStroke(new BasicStroke(3f));
        g.drawRect(cx, cy, cw, ch);

        // Pizzeria Header
        g.setColor(new Color(30, 40, 30));
        g.setFont(new Font("Serif", Font.BOLD, 26));
        g.drawString("FAZBEAR ENTERTAINMENT", cx + 40, cy + 50);

        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g.drawString("DATE: 11-12-1993", cx + cw - 180, cy + 50);

        // Pay to the order of
        g.setFont(new Font("Serif", Font.ITALIC, 20));
        g.drawString("Pay to the Order of:", cx + 40, cy + 120);

        g.setFont(new Font("Impact", Font.PLAIN, 28));
        g.drawString("Mike Schmidt", cx + 220, cy + 122);
        g.drawLine(cx + 215, cy + 128, cx + cw - 160, cy + 128);

        // Amount Box
        g.setColor(new Color(245, 250, 240));
        g.fillRect(cx + cw - 150, cy + 95, 110, 40);
        g.setColor(new Color(30, 40, 30));
        g.drawRect(cx + cw - 150, cy + 95, 110, 40);
        g.setFont(new Font("Impact", Font.BOLD, 22));
        g.drawString(isOvertime ? "$120.50" : "$120.00", cx + cw - 140, cy + 124);

        // Written amount
        g.setFont(new Font("Serif", Font.PLAIN, 18));
        g.drawString(isOvertime ? "One hundred twenty and 50/100 ----------------- Dollars"
                : "One hundred twenty and 00/100 ----------------- Dollars", cx + 40, cy + 180);

        // Note
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        if (isOvertime) {
            g.drawString("FOR: Overtime Security Services (+50 cents!)", cx + 40, cy + 240);
            g.drawString("EMPLOYEE OF THE MONTH!", cx + 40, cy + 280);
        } else {
            g.drawString("FOR: Security Guard Services", cx + 40, cy + 240);
            g.drawString("GREAT JOB, YOU ARE HIRED AGAIN NEXT WEEK!", cx + 40, cy + 280);
        }

        // Signature line
        g.drawLine(cx + cw - 240, cy + 320, cx + cw - 40, cy + 320);
        g.setFont(new Font("Serif", Font.ITALIC, 18));
        g.drawString("Fazbear Ent. Authorized", cx + cw - 230, cy + 342);
    }

    private void drawPinkSlip(Graphics2D g, int W, int H) {
        int pw = 680;
        int ph = 450;
        int px = (W - pw) / 2;
        int py = (H - ph) / 2;

        // Pink paper
        g.setColor(new Color(255, 215, 225));
        g.fillRect(px, py, pw, ph);
        g.setColor(new Color(180, 50, 80));
        g.setStroke(new BasicStroke(3f));
        g.drawRect(px, py, pw, ph);

        // Notice of Termination header
        g.setColor(new Color(180, 20, 40));
        g.setFont(new Font("Impact", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        String title = "NOTICE OF TERMINATION";
        g.drawString(title, px + (pw - fm.stringWidth(title)) / 2, py + 60);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        int textY = py + 120;
        g.drawString("EMPLOYEE: Mike Schmidt", px + 50, textY);
        g.drawString("POSITION: Night Security Guard", px + 50, textY + 35);
        g.drawString("EFFECTIVE: Immediately", px + 50, textY + 70);

        g.setColor(new Color(160, 20, 20));
        g.drawString("REASON FOR TERMINATION:", px + 50, textY + 125);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        g.drawString("- Tampering with the animatronics", px + 60, textY + 160);
        g.drawString("- General lack of hygiene", px + 60, textY + 195);
        g.drawString("- Extreme unprofessional conduct", px + 60, textY + 230);

        g.setFont(new Font("Impact", Font.BOLD, 32));
        g.setColor(new Color(200, 20, 20));
        g.drawString("YOU ARE FIRED!", px + 50, textY + 290);
    }
}
