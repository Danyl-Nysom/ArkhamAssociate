package com.danylnysom.arkhamassociate.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper implements PlayerStats {

    private static final String DB_NAME = "arkhamdb";

    public static final String PLAYER_TABLE = "players";
    public static final String GAME_TABLE = "games";
    public static final String INVESTIGATOR_TABLE = "investigators";

    private static final String COL_ID = "_id";
    public static final String COL_KEY = "key";
    public static final String COL_NAME = "name";
    public static final String COL_CREATION = "creation";
    public static final String COL_GAME = "game";
    public static final String COL_INVESTIGATOR = "investigator";
    public static final String COL_HOME = "home";
    public static final String COL_POSSESSIONS_FIXED = "pos_fixed";
    public static final String COL_POSSESSIONS_RANDOM = "pos_random";
    public static final String COL_ABILITY = "ability";
    public static final String COL_STATS = "stats";
    public static final String COL_MONEY = "money";
    public static final String COL_CLUES = "clues";
    public static final String COL_STORY = "story";
    public static final String COL_BLESSED = "blessed";
    public static final String COL_SANITY = "sanity";
    public static final String COL_STAMINA = "stamina";
    public static final String COL_SANITY_MAX = "max_sanity";
    public static final String COL_STAMINA_MAX = "max_stamina";

    private static final int VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + INVESTIGATOR_TABLE + " (" +
                COL_KEY + " INTEGER PRIMARY KEY, " +
                COL_ID + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_HOME + " TEXT, " +
                COL_STATS + " INTEGER, " +
                COL_POSSESSIONS_FIXED + " TEXT, " +
                COL_POSSESSIONS_RANDOM + " TEXT, " +
                COL_MONEY + " INTEGER, " +
                COL_CLUES + " INTEGER, " +
                COL_BLESSED + " INTEGER, " +
                COL_ABILITY + " TEXT, " +
                COL_STORY + " TEXT);");

        db.execSQL("CREATE TABLE " + GAME_TABLE + " (" +
                COL_KEY + " INTEGER PRIMARY KEY, " +
                COL_ID + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_CREATION + " INTEGER);");

        db.execSQL("CREATE TABLE " + PLAYER_TABLE + " (" +
                COL_KEY + " INTEGER PRIMARY KEY, " +
                COL_ID + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_STATS + " INTEGER, " +
                COL_GAME + " INTEGER, " +
                COL_INVESTIGATOR + " STRING, " +
                COL_MONEY + " INTEGER, " +
                COL_CLUES + " INTEGER, " +
                COL_BLESSED + " INTEGER, " +
                COL_SANITY + " INTEGER, " +
                COL_STAMINA + " INTEGER, " +
                COL_SANITY_MAX + " INTEGER, " +
                COL_STAMINA_MAX + " INTEGER, " +
                "FOREIGN KEY(" + COL_GAME + ") REFERENCES " + GAME_TABLE + "(" + COL_KEY + ")," +
                "FOREIGN KEY(" + COL_INVESTIGATOR + ") REFERENCES " + INVESTIGATOR_TABLE + "(" + COL_NAME + "));");

        addInvestigators(db);
    }

    private void addInvestigators(SQLiteDatabase db) {
        switch (VERSION) {
            case 1:
                addInvestigator("\"Ashcan\" Pete, the Drifter", "River Docks", 4, 6, 3, 6, 5, 5, 3, 3, 1,
                        "$1\n3 Clue tokens\n1 Ally (Duke)", "1 Common item\n1 Unique item\n1 Skill",
                        1, 3, 0,
                        "Scrounge - When Pete draws from the Common item, Unique item, or Spell deck, he may draw from either the top or the bottom of that deck, his choice. Pete may look at the bottom card of those decks at any time.",
                        "When you've lived on the streets as long as Pete has, you see things. Things that would drive braver men screaming into the night. But you also learn to be quiet, to stay hidden, and to play stupid if all else fails. It also helps to have a good dog, like Duke, to scare away the meaner elements of the street.\n" +
                                "Unfortunately, this time, Pete can't hide, and there's nothing Duke can do to protect him. His nightmares have been growing steadily worse over the last month, driving him all the way here... to Arkham. Even the whiskey isn't helping much anymore. Soon, he won't be able to sleep at all. Still, there are always opportunities for a man who knows how to stay quiet... as long as he isn't too picky.",
                        db);
                addInvestigator("Amanda Sharpe, the Student", "Bank of Arkham", 5, 5, 4, 4, 4, 4, 4, 4, 3,
                        "$1\n1 Clue token", "1 Common item\n1 Unique item\n2 Skills\n1 Spell",
                        1, 1, 0,
                        "Studious - Whenever Amanda draws one or more cards from the Skill deck, she draws one extra card and then discards one of the cards.",
                        "Amanda has been a student at Miskatonic University for 2 years now. On her way to talk to one of her professors last month, she saw a painting in the hallway that captured her attention with its hazy depiction of some horrible creature rising up out of the ocean. Ever since, Amanda has heard strange whispers in a foreign language whenever her attention drifts. More disturbingly, she has begun to dream of the vast green depths of the ocean and terrible alien cities that lie in its darkest crevasses.\n" +
                                "This evening, as she finishes her shift as a bank teller at the First Bank of Arkham, something out of the night calls to her-- something dark and sinister that leaves the feel of sea foam in her mind and makes her gasp with the effort of resisting it. Leaning against the brick wall of the bank, Amanda realizes that she has to find out what's happening to her or she's going to fall prey to whatever alien presence is invading her mind.",
                        db);
                addInvestigator("Bob Jenkins, the Salesman", "General Store", 4, 6, 5, 3, 4, 6, 3, 4, 1,
                        "$9", "2 Common items\n2 Unique items\n1 Skill",
                        9, 0, 0,
                        "Shrewd Dealer - Any Phase: Whenever Bob draws one or more cards from the Common item deck, he draws one extra card and then discards one of the cards.",
                        "As a traveling salesman, Bob is always on the go. But yesterday, he saw something that made him decide to stay in Arkham and miss his train. While he was in the General Store selling his wares, a robed man came in and bought several items, paying with old gold coins. Astounded, Bob turned to the shopkeeper for an explanation, but the man just ignored his questions, simply saying, \"That happens, sometimes.\"\n" +
                                "Now, Bob isn't leaving until he figures out where those gold coins came from. If he plays his cards right, maybe this will be the big score. Maybe he'll finally be able to retire and buy that boat he's had his eye on and spend the rest of his days fishing in a tropical paradise. Then again, maybe Bob will finally come to see that all that glitters is not gold.",
                        db);
                addInvestigator("Carolyn Fern, the Psychologist", "Arkham Asylum", 6, 4, 3, 3, 4, 4, 5, 5, 2,
                        "$7\n1 Clue token", "2 Unique items\n2 Common items\n1 Skill",
                        7, 1, 0,
                        "Psychology - Upkeep: Dr. Fern may restore 1 Sanity to herself or another character in her location. She cannot raise a character's Sanity higher than that character's maximum Sanity.",
                        "Carolyn is a first year resident at a sanitarium in Providence. Over the past six months, she has been studying the dreams of her patients using hypnosis. One patient in particular gave her vivid and disturbing descriptions of his dreams, right up until he was murdered with a strange knife that closely resembled something from one of his nightmares.\n" +
                                "Disturbed and frightened by his murder, Carolyn dug back through her notes, poring over them late into the night. Finally, she found some subtle clues that led her here, to Arkham, where he was previously an inmate in Arkham Asylum. Someone here has to know why a harmless man was murdered for talking about his dreams to his psychologist.",
                        db);
                addInvestigator("Darrell Simmons, the Photographer", "Newspaper", 4, 6, 5, 3, 5, 4, 3, 4, 2,
                        "$4\n1 Clue token\n1 Special (Retainer)", "1 Common item\n2 Unique items\n1 Skill",
                        4, 1, 0,
                        "Hometown Advantage - Town Encounter: When drawing location encounters in Arkham, Darrell draws two cards and may choose whichever one of the two he wants. This ability does not work when drawing gate encounters in Other Worlds.",
                        "Even while growing up in Arkham, Darrell always knew that there was something not quite right about the strange little town. After graduating from high school, he went to work for the Arkham Advertiser as a photographer, and in the years since, he's crawled over every square inch of the city.\n" +
                                "Last night, however, Darrell saw something horrible-- something that has shaken his world to its core and torn away the safe illusions we all foster to protect our minds and our souls. His editor says he was just seeing things, but as he leaves the newspaper building, he knows just what he saw and he intends to show the world! This time he'll be more careful. This time he'll take pictures and prove that things are not normal in Arkham.",
                        db);
                addInvestigator("Dexter Drake, the Magician", "Ye Olde Magick Shoppe", 5, 5, 5, 4, 4, 3, 5, 3, 2,
                        "$5\n1 Spell (Shrivelling)", "1 Common item\n1 Unique item\n2 Spells\n1 Skill",
                        5, 0, 0,
                        "Magical Gift - Any Phase: Whenever \"The Great\" Drake draws one or more cards from the Spell deck, he draws one extra card and then discards one of the cards.",
                        "After returning from his stint in the army during WWI, Dexter became a stage magician, and proved to be very successful at his trade, but he always longed to find real magic. As they say, be careful what you wish for. Years later, in a rundown store, Dexter came across a burnt and torn fragment of the Necronomicon itself. Intrigued by this ancient piece of occult knowledge, Dexter began to use his wealth in search of the truth about the ancient lore, and what he found horrified him.\n" +
                                "Now, the more he learns, the less he wants to know, but his studies have led him to believe that a great evil will soon arise in Arkham. He knows that he may well be the only person with the ability to stop this evil from swallowing the world, so he has come to that sleepy town to speak with the proprietor of Ye Olde Magick Shoppe, one of the few magic shops that contain true lore, and not merely the stage tricks he once studied.",
                        db);
                addInvestigator("Gloria Goldberg, the Author", "Velma's Diner", 6, 4, 4, 3, 3, 5, 4, 5, 2,
                        "$7\n2 Clue tokens", "2 Common items\n2 Spells\n1 Skill",
                        7, 2, 0,
                        "Psychic Sensitivity - Other World Encounter: When drawing gate encounters in Other Worlds, Gloria draws two cards that match the color of one of the Other World's encounter symbols, then chooses whichever one of the two she wants. This ability does not work when drawing location encounters in Arkham.",
                        "As a young girl, Gloria was haunted by terrible visions. After years of visiting doctors and some therapy, she learned to control her visions somewhat by writing stories. Her weird and disturbing fiction somehow spoke to the public in these troubled times, and has made her a bestselling writer.\n" +
                                "This evening, while leaving a book signing she's attending in Arkham, she was knocked to the ground by the most powerful vision she's ever experienced. Gloria saw the sky tear open, and a huge and monstrous form pour out of the very air itself, wreaking untold havoc and killing thousands. As she sat on the ground with her arms wrapped around herself, Gloria knew, somehow, that this vision was real, and that it would come to pass unless she did something about it.\n" +
                                "Now, she finds herself in a run-down diner, sipping coffee and trying to decide what to do.",
                        db);
                addInvestigator("Harvey Walters, the Professor", "Administration Building", 7, 3, 3, 5, 3, 3, 6, 4, 2,
                        "$5\n1 Clue token", "2 Unique items\n2 Spells\n1 Skill",
                        5, 1, 0,
                        "Strong Mind - Any Phase: Harvey reduces all Sanity losses he suffers by 1, to a minimum of 0.",
                        "Harvey is a visiting Professor at Miskatonic University. With Doctorates in History and Archaeology, he has uncovered several interesting artifacts over the years and learned a little of the arcane arts. Recently, by carefully studying the papers and talking to people in the streets, he has begun to detect a disturbance in the city-- something that could potentially herald the arrival of something unthinkable from beyond time and space.\n" +
                                "Checking his notes, Professor Walters prepares himself for one last trip into the streets of Arkham to confirm his theory. If he's right, it could spell the end of everything.",
                        db);
                addInvestigator("Jenny Barnes, the Dilettante", "Train Station", 6, 4, 3, 4, 4, 5, 4, 5, 1,
                        "$10", "2 Common items\n1 Unique item\n1 Spell\n1 Skill",
                        10, 0, 0,
                        "Trust Fund - Upkeep: Jenny gains $1.",
                        "Several months ago, Jenny was visiting Paris when she received a letter from her sister, Isabelle. In it, Isabelle rambled incoherently, writing about men in dark cloaks following her wherever she went, and of hoof prints in the woods, left by an enormous goat. The outside of the envelope was partially stained with blood, and it was mailed from Arkham. That was the last letter from Isabelle she received.\n" +
                                "Jenny has since returned to the States, coming to Arkham to find her missing sister. Stepping off the train from Boston into the dark autumn night, she believes that her sister was abducted by a strange cult, and is determined to find her and thwart the plans of those that took her... even if she has to save all of Arkham in the process.",
                        db);
                addInvestigator("Joe Diamond, the Private Eye", "Police Station", 4, 6, 6, 4, 5, 3, 3, 3, 3,
                        "$8\n3 Clue tokens\n1 Common item (.45 Automatic)", "2 Common items\n1 Skill",
                        8, 3, 0,
                        "Hunches - Any Phase: Joe rolls one extra bonus die when he spends a Clue token to add to a roll.",
                        "The job sounded simple enough-- pick up a statue at the Providence Museum and deliver it to a guy at the Silver Twilight Lodge. The money was good, and the dame who gave him the job seemed sincere.\n" +
                                "Sadly, things never seem to work out that easily for Joe. Now the statue is missing, two people are dead, strange cultists are on his tail, and all clues lead to Arkham. Lady Luck can be funny that way.\n" +
                                "He's already tried talking to the Sheriff, but that flatfoot proved to be worse than useless. Looks like it's once again going to be up to Joe Diamond to solve the case.",
                        db);
                addInvestigator("Kate Winthrop, the Scientist", "Science Building", 6, 4, 4, 5, 4, 3, 5, 4, 1,
                        "$7\n2 Clue tokens (Do not place a clue token on the Science Building to start the game.)",
                        "1 Common item\n1 Unique item\n2 Spells\n1 Skill",
                        7, 2, 0,
                        "Science! - Any Phase: Gates and monsters cannot appear in Kate's location due to her flux stabilizer. Monsters and gates do not disappear if she enters their location, however, and monsters can move into her location as usual.",
                        "A brilliant researcher, but a shy, lonely person, Kate Winthrop has been working at the Miskatonic Science Labs for 4 years now and her supervisor still doesn't know her name. That doesn't matter to her though, as she has been working to complete a private quest for most of that time. Almost 3 years ago, she watched as a device malfunctioned, and Professor Young, her long-time mentor and friend, was torn apart by an indistinct creature that shrieked and gibbered before vanishing into the night. Since then, she has delved into darker scientific studies, always hoping to find something that would allow her to find and defeat that creature along with others of its kind.\n" +
                                "Tonight, her research has finally paid off, allowing her to create a device that can defeat the alien beings she has detected in Arkham!",
                        db);
                addInvestigator("Mandy Thompson, the Researcher", "Library", 5, 5, 4, 5, 3, 5, 4, 3, 2,
                        "$6\n4 Clue tokens", "2 Common items\n1 Unique item\n1 Skill",
                        6, 4, 0,
                        "Research - Any Phase: Once per turn, Mandy can activate this ability after any investigator (including herself) makes a skill check. That investigator then re-rolls all of the dice rolled for that check that did not result in successes.",
                        "Mandy came to Arkham several years ago looking for work as a researcher for Miskatonic University. Since then, she has worked with many of the University professors, delving into esoteric tomes filled with scientific information, historical reports, and sometimes even occult ramblings.\n" +
                                "It was while reading an old book of prophecies last week that she first felt that she had stumbled onto something big. Mandy came to believe that certain signs and portents described in the book were taking place in Arkham right now-- omens that indicated the return of a terrible being referred to as an Ancient One, which would grind the cities of Man beneath its loathsome tread.\n" +
                                "Tonight, the full moon has turned blood red, which is the final omen of the return of the Ancient One. Slipping into the night, and armed with her knowledge of the prophecy, Mandy has decided to see if she can defy fate and stop these events from taking place.",
                        db);
                addInvestigator("Michael McGlen, the Gangster", "Ma's Boarding House", 3, 7, 5, 4, 6, 4, 3, 3, 1,
                        "$8\n2 Common items (Dynamite, Tommy Gun)", "1 Unique item\n1 Skill",
                        8, 0, 0,
                        "Strong Body - Any Phase: Michael reduces all Stamina losses he suffers by 1, to a minimum of 0.",
                        "As a soldier in the O'Bannion gang, Michael didn't really believe in all this voodoo mumbo jumbo around town. Or at least, he didn't until the night of the Foreman job, when he saw Fast Louie Farrell pulled screaming into the river by a scaly green creature. As they say, seeing is believing and Michael is starting to believe.\n" +
                                "Now, he has gathered his belongings together in the room that he rents at Ma's Boarding House. Louie was a friend of his, and he won't rest until he finds out what's happening in this town and avenges his buddy ...",
                        db);
                addInvestigator("Monterey Jack, the Archaeologist", "Curiositie Shoppe", 3, 7, 4, 3, 5, 3, 4, 5, 2,
                        "$7\n2 Common items (Bullwhip, .38 Revolver", "2 Unique item\n1 Skill",
                        7, 0, 0,
                        "Archaeology - Any Phase: Whenever Monterey draws one or more cards from the Unique item deck, he draws one extra card and then discards one of the cards.",
                        "Monterey has been a globe-trotting treasure hunter and adventurer for many years. Following in his father's footsteps, he's always tried to ensure that the specific value of his finds is preserved. Recently, he followed a lead on an odd prehistoric statue to Arkham. However, when he arrived, the man he came to buy the statue from was locked up in the asylum. Monterey was just about to give up and go home in disgust when a robed figure pushed past him.\n" +
                                "For just an instant, there was a flash of a silver pendant with a symbol on it Monterey would never forget. That symbol had been carved into his murdered father's forehead, and had haunted his dreams for years.\n" +
                                "Chasing after the mysterious figure, he turned a corner only to discover that he had lost his quarry.\n" +
                                "However, Monterey knows that somewhere in Arkham may lie the answer to the mystery of his father's murder, and he's not leaving until he finds it.",
                        db);
                addInvestigator("Sister Mary, the Nun", "South Church", 7, 3, 4, 4, 3, 4, 4, 6, 1,
                        "$0\nBlessing\n1 Common item (Cross)\n1 Unique item (Holy Water)",
                        "2 Spells\n1 Skill",
                        0, 0, 1,
                        "Guardian Angel - Any Phase: Sister Mary is never Lost in Time and Space. Instead, if her Sanity is 0, she returns to Arkham Asylum. If her Stamina is 0, she returns to St. Mary's Hospital. If neither her Sanity or her Stamina are 0, she returns to South Church.",
                        "Sister Mary has served the Church faithfully for many years, so when she was sent to Arkham to work with Father Michael, a man whose writings she had admired for many years, she felt that she was truly blessed. Now, after witnessing Father Michael's strange mood swings and seeing some of the bizarre practices that go on in this town, she's beginning to feel that she may have been a bit too hasty ...\n" +
                                "For instance, last night, there was a knock on the door of the church, and when she answered it, there was nothing but a handwritten journal laying on the steps outside. Reading it, she learned of strange cults and terrible creatures that lurk in the darkness. Worse, when she laughingly showed it to Father Michael, he turned pale and threw it into the fire, yelling at her to forget what she'd seen.\n" +
                                "Now, gathering her things and quietly leaving South Church, Sister Mary has decided to investigate this town, and in so doing, reaffirm her faith.",
                        db);
                addInvestigator("Vincent Lee, the Doctor", "St. Mary's Hospital", 5, 5, 3, 5, 3, 4, 5, 4, 2,
                        "$9\n1 Clue token", "2 Spells\n1 Skill",
                        9, 1, 0,
                        "Physician - Upkeep: Dr. Lee may restore 1 Stamina to himself or another character in his location. He cannot raise a character's Stamina higher than that character's maximum Stamina.",
                        "A Yale graduate of Medicine, Vincent has recently moved to Arkham from Boston to practice at St. Mary's Hospital. Since his coming to Arkham, he has seen far too many horrible and unexplained deaths - an elderly victim torn apart by unknown wild animals, a healthy young man whose heart exploded, and so many others. Their faces haunt his dreams, especially the young man's terrified expression. After all this, small wonder that Vincent has begun to wonder if there's something sinister going on in this quiet Massachusetts town.\n" +
                                "Tonight Dr. Lee made the decision to investigate the mysteries of Arkham and stop the strange deaths. He is determined to see this through, even if in so doing he becomes another puzzle for the next doctor who comes to Arkham.",
                        db);
        }
    }

    private void addInvestigator(String name, String home, int sanity, int stamina,
                                 int speed, int sneak, int fight, int will, int lore, int luck,
                                 int focus, String fixed, String random, int money, int clues,
                                 int blessed,
                                 String ability, String story, SQLiteDatabase db) {
        @SuppressWarnings("PointlessBitwiseExpression") int stats = (sanity << SANITY_SHIFT) + (stamina << STAMINA_SHIFT) +
                (speed << SPEED_SHIFT) + (sneak << SNEAK_SHIFT) +
                (fight << FIGHT_SHIFT) + (will << WILL_SHIFT) +
                (lore << LORE_SHIFT) + (luck << LUCK_SHIFT) +
                (focus << FOCUS_SHIFT);

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_HOME, home);
        values.put(COL_STATS, stats);
        values.put(COL_POSSESSIONS_FIXED, fixed);
        values.put(COL_POSSESSIONS_RANDOM, random);
        values.put(COL_ABILITY, ability);
        values.put(COL_MONEY, money);
        values.put(COL_CLUES, clues);
        values.put(COL_STORY, story);
        values.put(COL_BLESSED, blessed);

        db.insert(INVESTIGATOR_TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int getInvestigatorCount() {
        SQLiteDatabase db = getReadableDatabase();
        int count = db.query(INVESTIGATOR_TABLE, null, null, null, null, null, null).getCount();
        db.close();
        return count;
    }
}
