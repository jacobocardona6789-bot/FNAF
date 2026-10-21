package fnaf.model;

/**
 * Manages Phone Guy audio, transcripts, and the "MUTE CALL" button.
 */
public class PhoneGuy {
    private final int night;
    private boolean ringing = true;
    private boolean active = false;
    private boolean muted = false;
    private double timer = 0.0;
    private int currentLineIndex = 0;

    private static final String[][] SCRIPTS = {
            // Night 1
            {
                    "Hello, hello? Uh, I wanted to record a message for you to help you get settled in on your first night.",
                    "Um, I actually worked in that office before you. I'm finishing up my last week now, as a matter of fact.",
                    "So, I know it can be a bit overwhelming, but I'm here to tell you there's nothing to worry about. You'll do fine!",
                    "So, let's just focus on getting you through your first week. Okay?",
                    "Uh, let's see, first there's an introductory greeting from the company that I'm supposed to read.",
                    "Welcome to Freddy Fazbear's Pizza. A magical place for kids and grown-ups alike, where fantasy and fun come to life.",
                    "Fazbear Entertainment is not responsible for damage to property or person.",
                    "Upon discovering that damage or death has occurred, a missing person report will be filed within 90 days...",
                    "Blah blah blah, now that might sound bad, I know, but there's really nothing to worry about.",
                    "Uh, the animatronic characters here do get a bit quirky at night, but do I blame them? No! If I were forced to sing those same stupid songs for twenty years without a bath, I'd probably be a bit irritable at night too.",
                    "So, remember, these characters hold a special place in the hearts of children and we need to show them a little respect, right?",
                    "They're left in some kind of free roaming mode at night. Uh... Something about their servos locking up if they get turned off for too long.",
                    "Uh, now concerning your safety: the only real risk to you as a night watchman here is that the characters, if they happen to see you after hours, probably won't recognize you as a person.",
                    "They'll most likely see you as a metal endoskeleton without its costume on.",
                    "Now since that's against the rules here at Freddy Fazbear's Pizza, they'll probably try to... forcefully stuff you inside a Freddy Fazbear suit.",
                    "Um, yeah, they don't tell you these things when you sign up.",
                    "Check the cameras, and remember to close the doors only if absolutely necessary. Gotta conserve power.",
                    "Alright, good night, and I'll talk to you tomorrow!"
            },
            // Night 2
            {
                    "Uh, hello? Hello? Well, if you're hearing this, you made it to day two! Uh, congrats!",
                    "I won't talk quite as long this time since Freddy and his friends tend to become more active as the week progresses.",
                    "Uh, it might be a good idea to peek at those cameras while I talk just to make sure everyone's in their proper place.",
                    "You know, interestingly enough, Freddy himself doesn't come off stage very often. I heard he becomes much more active in the dark though, so hey, I guess that's one more reason not to run out of power, right?",
                    "I also want to emphasize the importance of using your door lights. There are blind spots in your camera view, and those blind spots happen to be right outside your doors.",
                    "So if you can't find something, or someone, on your cameras, be sure to check the door lights.",
                    "Uh, one more thing: check on the curtain in Pirate Cove from time to time.",
                    "The character in there seems unique in that he becomes more active if the cameras remain off for long periods of time. I guess he doesn't like being watched.",
                    "Anyway, I'm sure you have everything under control! Talk to you soon!"
            },
            // Night 3
            {
                    "Hello, hello! Hey, you're doing great! Most people don't last this long!",
                    "I mean, you know, they usually move on to other things by now. I'm not implying that they died. Uh, that's not what I meant.",
                    "Anyway, you better check those cameras tonight. Things are definitely heating up.",
                    "Oh, and another thing: Freddy Fazbear is sneaky. He tends to move when the cameras aren't looking directly at him.",
                    "And he doesn't show up in the door lights either! So if you hear him laughing, check CAM 4B to keep an eye on him.",
                    "Hang in there! You're almost through the week!"
            },
            // Night 4
            {
                    "Hello, hello? Hey! Hey, wow, day 4. I knew you could do it.",
                    "Uh, hey, listen, I may not be around to send you a message tomorrow. *bang bang bang*",
                    "It's-it's been a bad night here for me. *music box Toreador plays faintly in background*",
                    "Um, I-I'm kinda glad that I recorded my messages for you *bang bang* uh, when I did.",
                    "Uh, do me a favor. *bang bang* Maybe sometime, uh, you could check inside those suits in the back room? *screech*",
                    "I'm gonna try to hold out until someone checks. Maybe it won't be so bad. *Freddy laugh*",
                    "I always wondered what was in all those empty heads back there... *loud banging*",
                    "You know... *ominous chime* ...oh no... *loud jumpscare scream cuts off*"
            },
            // Night 5
            {
                    "...(garbled demonic reverse audio: '...as of late... joyful... the metal...')...",
                    "...(deep robotic chanting: '...it is sweet to see the light... the darkness consumes...')...",
                    "...(loud mechanical screech and static)..."
            }
    };

    public PhoneGuy(int night) {
        this.night = Math.max(1, Math.min(5, night));
    }

    public void update(double dtSeconds) {
        if (muted) return;

        timer += dtSeconds;
        if (ringing) {
            if (timer >= 4.0) { // Ring for 4 seconds then pick up
                ringing = false;
                active = true;
                timer = 0.0;
            }
        } else if (active) {
            String[] lines = getLines();
            // Advance subtitle lines every 6 seconds
            currentLineIndex = (int) (timer / 5.5);
            if (currentLineIndex >= lines.length) {
                active = false;
            }
        }
    }

    public void mute() {
        this.muted = true;
        this.ringing = false;
        this.active = false;
    }

    public boolean isRinging() {
        return ringing && !muted;
    }

    public boolean isActive() {
        return active && !muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public String getCurrentSubtitle() {
        if (!active || muted) return "";
        String[] lines = getLines();
        if (currentLineIndex < lines.length) {
            return lines[currentLineIndex];
        }
        return "";
    }

    private String[] getLines() {
        int idx = night - 1;
        if (idx >= 0 && idx < SCRIPTS.length) {
            return SCRIPTS[idx];
        }
        return new String[]{""};
    }
}
