// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView64;


/**
 * The Drum 64 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView64 extends AbstractDrumView64<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView64 (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, this.drumOctave < 1 ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, this.drumOctave > Scales.DRUM_OCTAVE_LOWER ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        if (buttonID == PushControlSurface.PUSH_BUTTON_REPEAT)
            return false;
        return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleDeleteButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
        this.model.getCursorClip ().clearRow (this.offsetY + playedPad);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSelectButton (final int playedPad)
    {
        final ICursorDevice drumDevice64 = this.model.getDrumDevice64 ();
        if (!drumDevice64.hasDrumPads ())
            return;

        // Do not reselect
        if (drumDevice64.getDrumPad (playedPad).isSelected ())
            return;

        final ICursorDevice cd = this.model.getCursorDevice ();
        if (cd.isNested ())
            cd.selectParent ();

        this.surface.getModeManager ().setActiveMode (Modes.MODE_DEVICE_LAYER);
        drumDevice64.selectDrumPad (playedPad);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
        for (int i = PushControlSurface.PUSH_BUTTON_SCENE1; i <= PushControlSurface.PUSH_BUTTON_SCENE8; i++)
            this.surface.updateButton (i, off);
    }
}