package com.grub4k.pipapo;

import java.util.HashMap;

public class Logic {
    enum Choice {
        ROCK,
        PAPER,
        SCISSORS,
        SPOCK,
        LIZARD,
    }

    private static HashMap<Choice, Choice[]> winningLookup = new HashMap<Choice, Choice[]>() {{
        put(Choice.ROCK, new Choice[] { Choice.SCISSORS, Choice.LIZARD });
        put(Choice.PAPER, new Choice[] { Choice.ROCK, Choice.SPOCK });
        put(Choice.SCISSORS, new Choice[] { Choice.PAPER, Choice.LIZARD });
        put(Choice.SPOCK, new Choice[] { Choice.ROCK, Choice.SCISSORS });
        put(Choice.LIZARD, new Choice[] { Choice.PAPER, Choice.SPOCK });
    }};

    public static Choice fromString(String value) {
        return Choice.valueOf(value.toUpperCase());
    }

    /**
     * Compare two choices and return which one would win
     * @param a The first item to compare
     * @param b The second item to compare
     * @return 0 if both are equal, 1 if a wins over b and -1 if b wins over a
     */
    public static int compare(Choice a, Choice b) {
        if (a == b) {
            return 0;
        }
        Choice[] winning = winningLookup.get(a);
        if (winning[0] == b || winning[1] == b) {
            return 1;
        }
        return -1;
    }
}
