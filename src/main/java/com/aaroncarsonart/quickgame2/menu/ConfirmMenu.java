package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.PlayerAction;

public class ConfirmMenu extends Menu {

    Callback confirmCallback;
    public ConfirmMenu(MenuView menuView, MenuLayout menuLayout, Callback cancelCallback, Callback confirmCallback) {
        super(menuView, menuLayout, cancelCallback);
        this.confirmCallback = confirmCallback;
    }

    @Override
    public void respondToPlayerInputs(PlayerAction playerAction) {
        switch (playerAction) {
            case CANCEL:
                Callback cancel = this.getCancelCallback();
                cancel.execute();
                break;
            case OK:
                confirmCallback.execute();
                break;
            default:
        }
    }
}
