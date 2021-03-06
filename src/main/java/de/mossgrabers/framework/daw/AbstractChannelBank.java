// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * An abstract channel bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractChannelBank implements IChannelBank
{
    protected static final int                  NOTE_OFF      = 0;
    protected static final int                  NOTE_ON       = 1;
    protected static final int                  NOTE_ON_NEW   = 2;

    protected int                               numTracks;
    protected int                               numScenes;
    protected int                               numSends;

    protected ITrack []                         tracks;
    protected ISceneBank                        sceneBank;

    protected final IValueChanger               valueChanger;
    protected final Set<NoteObserver>           noteObservers = new HashSet<> ();
    protected final Set<TrackSelectionObserver> observers     = new HashSet<> ();
    protected final int [] []                   noteCache;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractChannelBank (final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
    {
        this.valueChanger = valueChanger;

        this.numTracks = numTracks;
        this.numScenes = numScenes;
        this.numSends = numSends;

        this.noteCache = new int [numTracks] [];
        for (int i = 0; i < numTracks; i++)
        {
            this.noteCache[i] = new int [128];
            Arrays.fill (this.noteCache[i], NOTE_OFF);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void addTrackSelectionObserver (final TrackSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /**
     * Notify all registered track selection observers.
     *
     * @param trackIndex The index of the track which selection state has changed
     * @param isSelected True if selected otherwise false
     */
    protected void notifyTrackSelectionObservers (final int trackIndex, final boolean isSelected)
    {
        for (final TrackSelectionObserver observer: this.observers)
            observer.call (trackIndex, isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipRecording ()
    {
        for (int t = 0; t < this.numTracks; t++)
        {
            for (int s = 0; s < this.numScenes; s++)
            {
                if (this.tracks[t].getSlot (s).isRecording ())
                    return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public ITrack getTrack (final int index)
    {
        return this.tracks[index];
    }


    /** {@inheritDoc} */
    @Override
    public ITrack getSelectedTrack ()
    {
        for (int i = 0; i < this.numTracks; i++)
        {
            if (this.tracks[i].isSelected ())
                return this.tracks[i];
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedTrackColorEntry ()
    {
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack == null)
            return DAWColors.COLOR_OFF;
        final double [] color = selectedTrack.getColor ();
        return DAWColors.getColorIndex (color[0], color[1], color[2]);
    }


    /** {@inheritDoc} */
    @Override
    public String getColorOfFirstClipInScene (final int scene)
    {
        for (int t = 0; t < this.getNumTracks (); t++)
        {
            final ISlot slot = this.getTrack (t).getSlot (scene);
            if (slot.doesExist () && slot.hasContent ())
                return DAWColors.getColorIndex (slot.getColor ());
        }
        return DAWColors.DAW_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        if (this.sceneBank != null)
            this.sceneBank.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.sceneBank;
    }


    /** {@inheritDoc} */
    @Override
    public void launchScene (final int scene)
    {
        if (this.sceneBank != null)
            this.sceneBank.launchScene (scene);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollScenesUp ()
    {
        return this.sceneBank != null ? this.sceneBank.canScrollScenesUp () : false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollScenesDown ()
    {
        return this.sceneBank != null ? this.sceneBank.canScrollScenesDown () : false;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesUp ()
    {
        if (this.sceneBank != null)
            this.sceneBank.scrollScenesUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesDown ()
    {
        if (this.sceneBank != null)
            this.sceneBank.scrollScenesDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesPageUp ()
    {
        if (this.sceneBank != null)
            this.sceneBank.scrollScenesPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollScenesPageDown ()
    {
        if (this.sceneBank != null)
            this.sceneBank.scrollScenesPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public int getScenePosition ()
    {
        return this.sceneBank == null ? -1 : this.sceneBank.getScrollPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public void addNoteObserver (final NoteObserver observer)
    {
        this.noteObservers.add (observer);
    }


    /**
     * Notify all registered note observers.
     *
     * @param note The note which is playing or stopped
     * @param velocity The velocity of the note, note is stopped if 0
     */
    protected void notifyNoteObservers (final int note, final int velocity)
    {
        for (final NoteObserver noteObserver: this.noteObservers)
            noteObserver.call (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public int getNumTracks ()
    {
        return this.numTracks;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumScenes ()
    {
        return this.numScenes;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSends ()
    {
        return this.numSends;
    }
}
