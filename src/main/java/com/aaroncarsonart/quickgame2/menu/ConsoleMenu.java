package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.PlayerAction;
import javafx.scene.input.KeyCode;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConsoleMenu extends Menu implements KeyListener {

    private ArrayList<Character> characterBuffer;
    private int maxLength;
    private Consumer<String> okCallback;

    public ConsoleMenu(MenuView menuView, MenuLayout menuLayout, Callback cancelCallback, Consumer<String> okCallback, int maxLength) {
        super(menuView, menuLayout, cancelCallback);
        this.characterBuffer = new ArrayList<>();
//        this.characterBuffer.add(' ');
        this.maxLength = maxLength;
        this.okCallback = okCallback;
    }

    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println(KeyEvent.VK_BACK_SPACE + " " + KeyEvent.VK_DELETE);
//        System.out.println("KEY_TYPED e.getKeyCode() = '"  + e.getKeyCode() + "'");
//        System.out.println("KEY_TYPED e.getKeyChar() = '" + e.getKeyChar() + "'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("KEY_PRESSED e.getKeyCode() = '"  + e.getKeyCode() + "'");
        System.out.println("KEY_PRESSED e.getKeyChar() = '" + e.getKeyChar() + "'");

        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                getCancelCallback().execute();
                break;
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_ALT:
                break;
            case KeyEvent.VK_LEFT:
                if (index > 0) {
                    index --;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (index < characterBuffer.size()) {
                    index ++;
                }
                break;
            case KeyEvent.VK_BACK_SPACE:
                if (!characterBuffer.isEmpty() && index != 0) {
                    characterBuffer.remove(index - 1);
                    index--;
                }
                break;
            case KeyEvent.VK_ENTER:
                StringBuilder sb = new StringBuilder(characterBuffer.size());
                for (Character c : characterBuffer) {
                    sb.append(c);
                }
                String str = sb.toString();
                okCallback.accept(str);
                break;
            default:
                char c = e.getKeyChar();
                if (characterBuffer.size() < maxLength) {
                    characterBuffer.add(index, c);
                    index++;
                }
                break;
        }
        System.out.println("index: " + index);
        System.out.println("cBufferSize: " + characterBuffer.size());
        System.out.print("characterBuffer: \"");
        for (Character c : characterBuffer){
            System.out.print(c);
        }
        System.out.println("\"");

    }

    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println("KEY_RELEASED e.getKeyCode() = '"  + e.getKeyCode() + "'");
//        System.out.println("KEY_RELEASED e.getKeyChar() = '" + e.getKeyChar() + "'");
    }

    public List<Character> getCharacterBuffer() {
        return characterBuffer;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public void respondToPlayerInputs(PlayerAction playerAction) {
    }
}
