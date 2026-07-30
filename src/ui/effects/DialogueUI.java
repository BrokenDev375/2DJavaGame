package ui.effects;

import main.GamePanel;
import main.GameState;
import ui.base.BaseUI;
import java.awt.*;

public class DialogueUI extends BaseUI {

    private String[] lines;
    private int index = 0, charIndex = 0, frame = 0;
    private final int SPEED = 2;
    private final StringBuilder text = new StringBuilder();
    private boolean finished = false;
    private boolean prevPressed = false; // tránh việc giữ phím E tạo nhiều lần nhấn

    public DialogueUI(GamePanel gp) { super(gp); }

    // Bắt đầu một đoạn hội thoại mới
    public void startDialogue(String[] src) {
        this.lines = (src == null || src.length == 0) ? new String[]{""} : src;
        this.index = 0;
        this.charIndex = 0;
        this.frame = 0;
        this.finished = false;
        this.prevPressed = false;
        text.setLength(0);
        gp.setGameState(GameState.DIALOGUE);
    }
    // Kiểm tra UI đang hoạt động hay không
    public boolean isActive() {
        return gp.getGameState() == GameState.DIALOGUE && lines != null && index < lines.length;
    }

    // Nhảy trang hoặc hiển thị hết dòng hiện tại
    public void nextPage() {
        if (lines == null) return;
        String line = lines[index] == null ? "" : lines[index];
        if (!finished) {
            // Hiển thị toàn bộ dòng ngay lập tức
            text.setLength(0);
            text.append(line);
            charIndex = line.length();
            finished = true;
        } else {
            nextLine();
        }
    }

    @Override
    public void update() {
        if (lines == null || index >= lines.length) return;
        String line = lines[index] == null ? "" : lines[index];

        // Hiệu ứng gõ chữ từng ký tự
        if (!finished && ++frame >= SPEED) {
            if (charIndex < line.length()) {
                text.append(line.charAt(charIndex++));
            } else {
                finished = true;
            }
            frame = 0;
        }
        // Lấy input người chơi
        var p = gp.getEntityManager().getPlayer();
        if (p == null) return;

        boolean pressed = p.isTalkPressed();

        // Chỉ xử lý khi phím E được nhấn 1 lần (tránh spam)
        if (pressed && !prevPressed) {
            if (!finished) {
                // Bỏ qua hiệu ứng gõ và hiển thị toàn bộ câu
                text.setLength(0);
                text.append(line);
                charIndex = line.length();
                finished = true;
            } else {
                nextLine();
            }
        }

        prevPressed = pressed;
    }

    // Chuyển sang dòng hội thoại tiếp theo
    private void nextLine() {
        index++;
        if (lines != null && index < lines.length && lines[index] != null) {
            // Reset để chuẩn bị in dòng tiếp theo
            text.setLength(0);
            charIndex = 0;
            frame = 0;
            finished = false;
        } else {
            endDialogue();
        }
    }

    // Kết thúc hội thoại
    private void endDialogue() {
        text.setLength(0);
        lines = null;
        finished = false;
        index = charIndex = 0;
        // Trả về trạng thái PLAY
        gp.setGameState(GameState.PLAY);
        // Reset phím E để tránh NPCInteract đọc lại ngay sau khi thoát hội thoại
        var p = gp.getEntityManager().getPlayer();
        if (p != null) {
            p.resetTalkInput();
        }
        prevPressed = false;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!isActive()) return;
        int x = gp.tileSize();
        int y = gp.screenHeight() - gp.tileSize() * 4;
        int w = gp.screenWidth() - 2 * x;
        int h = gp.tileSize() * 3;
        // Nền hộp thoại
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(x, y, w, h, 20, 20);
        // Viền hộp thoại
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x, y, w, h, 20, 20);
        // Text hội thoại
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        g2.setColor(Color.white);
        drawMultiline(g2, text.toString(), x + gp.tileSize() / 2, y + gp.tileSize(), w - gp.tileSize());

        // Hint chuyển trang
        if (finished) {
            boolean hasMore = (lines != null && index < lines.length - 1);
            String hint = hasMore ? "[E] to continue" : "[E] to close";
            FontMetrics fm = g2.getFontMetrics();
            int hintWidth = fm.stringWidth(hint);
            int paddingX = gp.tileSize() / 2;
            int paddingY = gp.tileSize() / 3;
            int hintX = x + w - paddingX - hintWidth;
            int hintY = y + h - paddingY;
            g2.setColor(new Color(255, 255, 255, 210));
            g2.drawString(hint, hintX, hintY);
        }
    }
    // Vẽ text xuống dòng tự động
    private void drawMultiline(Graphics2D g2, String s, int x, int y, int width) {
        FontMetrics fm = g2.getFontMetrics();
        int lh = fm.getHeight();
        StringBuilder line = new StringBuilder();

        for (String word : s.split(" ")) {
            if (fm.stringWidth(line + word) < width)
                line.append(word).append(" ");
            else {
                g2.drawString(line.toString(), x, y);
                line = new StringBuilder(word + " ");
                y += lh;
            }
        }
        g2.drawString(line.toString(), x, y);
    }
    @Override
    public boolean shouldRenderIn(GameState st) {
        return st == GameState.DIALOGUE;
    }

    @Override
    public boolean shouldUpdate() {
        return gp.getGameState() == GameState.DIALOGUE;
    }
}
