package succ.basetypes;

import falsepattern.reflectionhelper.ClassTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import succ.TestUtilities;
import succ.style.FileStyle;

public class SaveLoad_StringTests {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null", "   boobs", "boobs   ", "   boobs   ", "\"", "\"\"", "\"\"\"",
    "#", "##", "###", "this is #not a #comment!", "\uD83C\uDF46", multiLineString, multiLineStringWithPoundSigns,
    multiLineStringWithDoubleQuotes, multiLineStringWithLeadingTrailingSpaces, veryLongString, poem, genemoji})
    @NullSource
    public void saveLoad_String(String savedValue) {
        TestUtilities.performSaveLoadTest(new ClassTree<>(String.class), savedValue);
    }

    @Test
    public void saveLoad_String_NewLine() {
        TestUtilities.performSaveLoadTest(new ClassTree<>(String.class), "\n");
    }

    @Test
    public void saveLoad_String_NewLinex3() {
        TestUtilities.performSaveLoadTest(new ClassTree<>(String.class), "\n\n\n");
    }

    private static final String multiLineString = "\n" +
            "this is a\n" +
            "string with multiple lines\n";

    private static final String multiLineStringWithPoundSigns = "\n" +
            "this is a\n" +
            "string with multiple lines\n" +
            "and #pound #signs\n" +
            "####\n" +
            "#yolo\n" +
            "yolo#\n";

    private static final String multiLineStringWithDoubleQuotes = "\n" +
            "this is a\n" +
            "string with multiple lines\n" +
            "and \"\"double quotes\"\"\n" +
            "\"\"\"\"\n" +
            "\"\"\"\"\"\"\n" +
            "\"\"yolo\n" +
            "yolo\"\"\n" +
            "\"\"yolo\"\"\n";

    private static final String multiLineStringWithLeadingTrailingSpaces = "\n" +
            "this is a\n" +
            "string with multiple lines\n" +
            "   and leading spaces\n" +
            "and trailing spaces    \n" +
            "    and both!    \n";

    private static final String veryLongString = "What the fuck did you just fucking say about me, you little bitch? I'll have you know I graduated top of my public class in the Navy Seals, and I've been involved in numerous secret raids on Al-Quaeda, and I have over 300 confirmed kills. I am trained in gorilla warfare and I'm the top sniper in the entire US armed forces. You are nothing to me but just another target. I will wipe you the fuck out with precision the likes of which has never been seen before on this Earth, mark my fucking words. You think you can get away with saying that shit to me over the Internet? Think again, fucker. As we speak I am contacting my secret network of spies across the USA and your IP is being traced right now so you better prepare for the storm, maggot. The storm that wipes out the pathetic little thing you call your life. You're fucking dead, kid. I can be anywhere, anytime, and I can kill you in over seven hundred ways, and that's just with my bare hands. Not only am I extensively trained in unarmed combat, but I have access to the entire arsenal of the United States Marine Corps and I will use it to its full extent to wipe your miserable ass off the face of the continent, you little shit. If only you could have known what unholy retribution your little \"clever\" comment was about to bring down upon you, maybe you would have held your fucking tongue. But you couldn't, you didn't, and now you're paying the price, you goddamn idiot. I will shit fury all over you and you will drown in it. You're fucking dead, kiddo.";

    // [i carry your heart with me(i carry it in]
    // by E. E. Cummings
    private static final String poem = "\n" +
            "i carry your heart with me(i carry it in\n" +
            "my heart)i am never without it(anywhere\n" +
            "i go you go,my dear;and whatever is done\n" +
            "by only me is your doing,my darling)\n" +
            "                                                      i fear\n" +
            "no fate(for you are my fate,my sweet)i want\n" +
            "no world(for beautiful you are my world,my true)\n" +
            "and it’s you are whatever a moon has always meant\n" +
            "and whatever a sun will always sing is you\n" +
            "here is the deepest secret nobody knows\n" +
            "(here is the root of the root and the bud of the bud\n" +
            "and the sky of the sky of a tree called life;which grows\n" +
            "higher than soul can hope or mind can hide)\n" +
            "and this is the wonder that's keeping the stars apart\n" +
            "i carry your heart(i carry it in my heart)\n";

    private static final String genemoji = "\n" +
            "1 In the beginning\uD83C\uDF04 God ⛏️\uD83D\uDEE0️created⚒️\uD83D\uDD28 the heavens\uD83C\uDF24️ and the \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️. 2 Now the \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️ was formless and empty, \uD83C\uDF1Adarkness\uD83C\uDF1A was over the surface of the deep, and the \uD83D\uDC7BSpirit\uD83D\uDC7B of God was hovering over the \uD83C\uDF0A\uD83D\uDCA7waters\uD83D\uDCA6\uD83D\uDCA6\uD83D\uDCA6.\n" +
            "3 And God said\uD83D\uDDE3️, “Let there be\uD83D\uDC1D light\uD83D\uDCA1,” and there was light\uD83D\uDCA1. 4 God saw\uD83D\uDC41️ that the \uD83C\uDF1Dlight\uD83C\uDF1D was good\uD83D\uDC4D\uD83D\uDC4C\uD83D\uDC4C, and he separated the \uD83C\uDF1Dlight\uD83C\uDF1D from the \uD83C\uDF1Adarkness\uD83C\uDF1A. 5 God called\uD83D\uDCDE the \uD83C\uDF1Dlight\uD83C\uDF1D “day☀️,” and the \uD83C\uDF1Adarkness\uD83C\uDF1A he called\uD83D\uDCDE “night\uD83C\uDF19.” And there was \uD83C\uDF19evening, and there was ☀️morning—the \uD83E\uDD47first ☀️day☀️.\n" +
            "6 And God said\uD83D\uDDE3️, “Let there be\uD83D\uDC1D a\uD83C\uDD70️ vault ➡️between⬅️ the \uD83C\uDF0A\uD83D\uDCA7waters\uD83D\uDCA6\uD83D\uDCA6 to2️⃣ separate \uD83C\uDF0A\uD83D\uDCA7water\uD83D\uDCA6\uD83D\uDCA6 from \uD83C\uDF0A\uD83D\uDCA7water\uD83D\uDCA6\uD83D\uDCA6.” 7 So God ⛏️⚒️made\uD83D\uDEE0️\uD83D\uDD28 the vault and separated the \uD83C\uDF0A\uD83D\uDCA7water\uD83D\uDCA6\uD83D\uDCA6 under the vault from the \uD83C\uDF0A\uD83D\uDCA7water\uD83D\uDCA6\uD83D\uDCA6 above it. And it was so. 8 God called\uD83D\uDCDE the vault “sky\uD83C\uDF25️.” And there was \uD83C\uDF19evening, and there was ☀️morning—the \uD83E\uDD48second ☀️day☀️.\n" +
            "9 And God said\uD83D\uDDE3️, “Let the \uD83C\uDF0A\uD83D\uDCA7water\uD83D\uDCA6\uD83D\uDCA6 under the \uD83C\uDF25️sky be\uD83D\uDC1D gathered to2️⃣ one1️⃣ place, and let dry\uD83C\uDF35 ground appear.” And it was so. 10 God called\uD83D\uDCDE the dry ground “land\uD83D\uDDFA️,” and the gathered \uD83C\uDF0A\uD83D\uDCA7waters\uD83D\uDCA6\uD83D\uDCA6 he called\uD83D\uDCDE “seas\uD83D\uDDFA️.” And God saw\uD83D\uDC41️ that it was good\uD83D\uDC4D\uD83D\uDC4C\uD83D\uDC4C.\n" +
            "11 Then God said\uD83D\uDDE3️, “Let the land\uD83D\uDDFA️ produce \uD83C\uDF31\uD83C\uDF32vegetation\uD83C\uDF33\uD83C\uDF34: seed-bearing plants\uD83C\uDF31 and \uD83C\uDF32\uD83C\uDF33trees\uD83C\uDF34\uD83C\uDF34 on the land\uD83D\uDDFA️ that bear \uD83C\uDF47\uD83C\uDF48\uD83C\uDF49\uD83C\uDF4A\uD83C\uDF4B\uD83C\uDF4C\uD83C\uDF4Dfruit\uD83C\uDF4E\uD83C\uDF4F\uD83C\uDF50\uD83C\uDF51\uD83C\uDF52\uD83C\uDF53\uD83E\uDD5D with seed in it, according to2️⃣ their various kinds.” And it was so. 12 The land\uD83D\uDDFA️ produced \uD83C\uDF31\uD83C\uDF32vegetation\uD83C\uDF33\uD83C\uDF34: plants\uD83C\uDF31 bearing seed according to2️⃣ their kinds and \uD83C\uDF32\uD83C\uDF33trees\uD83C\uDF34\uD83C\uDF34 \uD83D\uDC3Bbearing\uD83D\uDC3C \uD83C\uDF47\uD83C\uDF48\uD83C\uDF49\uD83C\uDF4A\uD83C\uDF4B\uD83C\uDF4C\uD83C\uDF4Dfruit\uD83C\uDF4E\uD83C\uDF4F\uD83C\uDF50\uD83C\uDF51\uD83C\uDF52\uD83C\uDF53\uD83E\uDD5D with seed in it according to2️⃣ their kinds. And God saw\uD83D\uDC41️ that it was good\uD83D\uDC4D\uD83D\uDC4C\uD83D\uDC4C. 13 And there was evening\uD83C\uDF19, and there was morning☀️—the \uD83E\uDD49third ☀️day☀️.\n" +
            "14 And God said\uD83D\uDDE3️, “Let there be\uD83D\uDC1D \uD83C\uDF1Dlights\uD83C\uDF1D in the vault of the sky\uD83C\uDF25️ to2️⃣ separate the ☀️day☀️ from the \uD83C\uDF19night\uD83C\uDF19, and let them serve as signs to2️⃣ mark sacred ⌚⏰⏱️times⏲️\uD83D\uDD70️\uD83D\uDD5B, and days\uD83D\uDCC6 and years\uD83D\uDCC5, 15 and let them be\uD83D\uDC1D \uD83C\uDF1Dlights\uD83C\uDF1D in the vault of the sky\uD83C\uDF25️ to2️⃣ give \uD83C\uDF1Dlight\uD83C\uDF1D on the \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️.” And it was so. 16 God ⛏️⚒️made\uD83D\uDEE0️\uD83D\uDD28 two2️⃣ great \uD83C\uDF1Dlights\uD83C\uDF1D—the greater \uD83C\uDF1Elight☀️ to govern the ☀️day☀️ and the lesser \uD83C\uDF1Blight\uD83C\uDF1C to govern the \uD83C\uDF19night\uD83C\uDF19. He also made the ✨stars✨. 17 God set them in the vault of the sky\uD83C\uDF25️ to give \uD83C\uDF1Dlight\uD83C\uDF1D on the \uD83C\uDF0F\uD83D\uDDFA️earth\uD83C\uDF0D\uD83C\uDF0E, 18 to2️⃣ govern the ☀️day☀️ and the \uD83C\uDF19night\uD83C\uDF19, and to2️⃣ separate \uD83C\uDF1Dlight\uD83C\uDF1D from \uD83C\uDF1Adarkness\uD83C\uDF1A. And God saw\uD83D\uDC41️ that it was good\uD83D\uDC4D\uD83D\uDC4C\uD83D\uDC4C. 19 And there was evening\uD83C\uDF19, and there was morning☀️—the 4️⃣fourth ☀️day☀️.\n" +
            "20 And God said\uD83D\uDDE3️, “Let the \uD83C\uDF0A\uD83D\uDCA7water\uD83D\uDCA6\uD83D\uDCA6 teem with living creatures, and let \uD83E\uDD83\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDD4A️\uD83E\uDD85birds\uD83D\uDC24\uD83D\uDC25\uD83D\uDC26\uD83D\uDC27\uD83E\uDD86\uD83E\uDD89 fly above\uD83D\uDD1D the \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️ across the vault of the sky\uD83C\uDF25️.” 21 So God ⛏️⚒️created\uD83D\uDEE0️\uD83D\uDD28 the great creatures of the \uD83D\uDDFA️sea and every living thing with which the \uD83C\uDF0A\uD83D\uDCA7water\uD83D\uDCA6\uD83D\uDCA6 teems and that moves about in it, according to2️⃣ their kinds, and every winged \uD83E\uDD83\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDC24\uD83D\uDC25bird\uD83D\uDC26\uD83D\uDC27\uD83D\uDD4A️\uD83E\uDD85\uD83E\uDD86\uD83E\uDD89 according to2️⃣ its kind. And God saw\uD83D\uDC41️ that it was good\uD83D\uDC4D\uD83D\uDC4C\uD83D\uDC4C. 22 God \uD83D\uDE07blessed\uD83D\uDE07 them and said\uD83D\uDDE3️, “Be \uD83C\uDF47\uD83C\uDF48\uD83C\uDF49\uD83C\uDF4A\uD83C\uDF4B\uD83C\uDF4C\uD83C\uDF4Dfruitful\uD83C\uDF4E\uD83C\uDF4F\uD83C\uDF50\uD83C\uDF51\uD83C\uDF52\uD83C\uDF53\uD83E\uDD5D and increase➕ in number#️⃣ and fill the \uD83C\uDF0A\uD83D\uDCA7water\uD83D\uDCA6\uD83D\uDCA6 in the seas\uD83D\uDDFA️, and let the \uD83E\uDD83\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDC24\uD83D\uDC25birds\uD83D\uDC26\uD83D\uDC27\uD83D\uDD4A️\uD83E\uDD85\uD83E\uDD86\uD83E\uDD89 increase➕ on\uD83D\uDD1B the \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️.” 23 And there was evening\uD83C\uDF19, and there was morning☀️—the 5️⃣fifth ☀️day☀️.\n" +
            "24 And God said\uD83D\uDDE3️, “Let the \uD83D\uDDFA️land produce living creatures according to2️⃣ their kinds: the \uD83D\uDC2E\uD83D\uDC04\uD83D\uDC37\uD83D\uDC16\uD83D\uDC17\uD83D\uDC2A\uD83D\uDC2Blivestock\uD83D\uDC0F\uD83D\uDC11\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDC24\uD83D\uDC25, the creatures that move along the \uD83D\uDC2B\uD83D\uDC2Aground\uD83D\uDC34\uD83D\uDC0E, and the \uD83D\uDC2C\uD83D\uDC1F\uD83E\uDD88\uD83D\uDC19\uD83D\uDC28\uD83D\uDC3C\uD83D\uDC3A\uD83E\uDD8A\uD83E\uDD81\uD83D\uDC2Fwild\uD83E\uDD80\uD83D\uDC0D\uD83D\uDC27\uD83E\uDD8C\uD83D\uDC18 \uD83E\uDD8F\uD83D\uDC3F️\uD83E\uDD85\uD83E\uDD91\uD83D\uDC0Aanimals\uD83D\uDC0C\uD83E\uDD82\uD83E\uDD87\uD83D\uDC3B\uD83D\uDC05\uD83D\uDC06\uD83D\uDC12\uD83E\uDD8D\uD83D\uDC33\uD83D\uDC0B, each according to2️⃣ its kind.” And it was so. 25 God ⛏️⚒️made\uD83D\uDEE0️\uD83D\uDD28 the \uD83D\uDC2C\uD83D\uDC1F\uD83E\uDD88\uD83D\uDC19\uD83D\uDC28\uD83D\uDC3C\uD83D\uDC3A\uD83E\uDD8A\uD83E\uDD81\uD83D\uDC2Fwild\uD83E\uDD80\uD83D\uDC0D\uD83D\uDC27\uD83E\uDD8C\uD83D\uDC18 \uD83E\uDD8F\uD83D\uDC3F️\uD83E\uDD85\uD83E\uDD91\uD83D\uDC0Aanimals\uD83D\uDC0C\uD83E\uDD82\uD83E\uDD87\uD83D\uDC3B\uD83D\uDC05\uD83D\uDC06\uD83D\uDC12\uD83E\uDD8D\uD83D\uDC33\uD83D\uDC0B according to2️⃣ their kinds, the \uD83D\uDC2E\uD83D\uDC04\uD83D\uDC37\uD83D\uDC16\uD83D\uDC17\uD83D\uDC2A\uD83D\uDC2Blivestock\uD83D\uDC0F\uD83D\uDC11\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDC24\uD83D\uDC25 according to2️⃣ their kinds, and all the creatures that move along the ground according to2️⃣ their kinds. And God saw\uD83D\uDC41️ that it was good\uD83D\uDC4D\uD83D\uDC4C\uD83D\uDC4C.\n" +
            "26 Then God said\uD83D\uDDE3️, “Let us ⛏️⚒️make\uD83D\uDEE0️\uD83D\uDD28 mankind in our image\uD83D\uDDBC️, in our likeness, so that they may rule over the \uD83E\uDD88\uD83D\uDC19fish\uD83E\uDD80\uD83E\uDD90 in the sea\uD83D\uDDFA️ and the \uD83E\uDD83\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDC24\uD83D\uDC25birds\uD83D\uDC26\uD83D\uDC27\uD83D\uDD4A️\uD83E\uDD85\uD83E\uDD86\uD83E\uDD89 in the sky\uD83C\uDF25️, over the \uD83D\uDC2E\uD83D\uDC04\uD83D\uDC37\uD83D\uDC16\uD83D\uDC17\uD83D\uDC2A\uD83D\uDC2Blivestock\uD83D\uDC0F\uD83D\uDC11\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDC24\uD83D\uDC25 and all the \uD83D\uDC2C\uD83D\uDC1F\uD83E\uDD88\uD83D\uDC19\uD83D\uDC28\uD83D\uDC3C\uD83D\uDC3A\uD83E\uDD8A\uD83E\uDD81\uD83D\uDC2Fwild\uD83E\uDD80\uD83D\uDC0D\uD83D\uDC27\uD83E\uDD8C\uD83D\uDC18 \uD83E\uDD8F\uD83D\uDC3F️\uD83E\uDD85\uD83E\uDD91\uD83D\uDC0Aanimals\uD83D\uDC0C\uD83E\uDD82\uD83E\uDD87\uD83D\uDC3B\uD83D\uDC05\uD83D\uDC06\uD83D\uDC12\uD83E\uDD8D\uD83D\uDC33\uD83D\uDC0B, and over all the creatures that move along the ground.”\n" +
            "27 So God ⛏️⚒️created\uD83D\uDEE0️\uD83D\uDD28 mankind in his own image\uD83D\uDDBC️,\n" +
            "    in the image\uD83D\uDDBC️ of God he ⚒️⛏️created\uD83D\uDD28\uD83D\uDEE0️ them;\n" +
            "    male♂️ and female♀️ he ⛏️⚒️created\uD83D\uDEE0️\uD83D\uDD28 them.\n" +
            "28 God \uD83D\uDE07blessed\uD83D\uDE07\uD83D\uDE4F them and said\uD83D\uDDE3️ to2️⃣ them, “Be\uD83D\uDC1D \uD83C\uDF47\uD83C\uDF48\uD83C\uDF49\uD83C\uDF4A\uD83C\uDF4B\uD83C\uDF4C\uD83C\uDF4Dfruitful\uD83C\uDF4E\uD83C\uDF4F\uD83C\uDF50\uD83C\uDF51\uD83C\uDF52\uD83C\uDF53\uD83E\uDD5D and increase➕ in number#️⃣; fill the \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️ and subdue it. Rule over the \uD83E\uDD88\uD83D\uDC19fish\uD83E\uDD80\uD83E\uDD90 in the sea\uD83D\uDDFA️ and the \uD83E\uDD83\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDC24\uD83D\uDC25birds\uD83D\uDC26\uD83D\uDC27\uD83D\uDD4A️\uD83E\uDD85\uD83E\uDD86\uD83E\uDD89 in the sky\uD83C\uDF25️ and over every living creature that moves on the ground.”\n" +
            "29 Then God said\uD83D\uDDE3️, “I\uD83D\uDC40 give you every seed-bearing plant\uD83C\uDF31 on the face\uD83D\uDE0E of the whole \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️ and every \uD83C\uDF32\uD83C\uDF33tree\uD83C\uDF34\uD83C\uDF34 that has \uD83C\uDF47\uD83C\uDF48\uD83C\uDF49\uD83C\uDF4A\uD83C\uDF4B\uD83C\uDF4C\uD83C\uDF4Dfruit\uD83C\uDF4E\uD83C\uDF4F\uD83C\uDF50\uD83C\uDF51\uD83C\uDF52\uD83C\uDF53\uD83E\uDD5D with seed in it. They will be \uD83D\uDC1Dyours for4️⃣ \uD83C\uDF49\uD83E\uDD55\uD83C\uDF36️\uD83C\uDF44\uD83E\uDD52\uD83E\uDD5C\uD83C\uDF57\uD83C\uDF5E\uD83E\uDD56\uD83C\uDF5F\uD83E\uDD5E\uD83E\uDDC0\uD83C\uDF2D\uD83C\uDF2E\uD83E\uDD59\uD83E\uDD5Afood\uD83E\uDD58\uD83C\uDF72\uD83E\uDD57\uD83C\uDF7F\uD83C\uDF71\uD83C\uDF58\uD83C\uDF5B\uD83C\uDF5C\uD83C\uDF5D\uD83C\uDF61\uD83C\uDF66\uD83C\uDF62\uD83C\uDF68\uD83C\uDF63\uD83C\uDF69\uD83C\uDF82\uD83C\uDF6A\uD83C\uDF70☕\uD83E\uDD5B\uD83C\uDF7A\uD83C\uDF6C\uD83C\uDF6E\uD83E\uDD43\uD83C\uDF76. 30 And to2️⃣ all the beasts\uD83D\uDC79\uD83D\uDC7A of the \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️ and all the \uD83E\uDD83\uD83D\uDC14\uD83D\uDC13\uD83D\uDC23\uD83D\uDC24\uD83D\uDC25birds\uD83D\uDC26\uD83D\uDC27\uD83D\uDD4A️\uD83E\uDD85\uD83E\uDD86\uD83E\uDD89 in the sky\uD83C\uDF25️ and all the creatures that move along the ground—everything that has the breath\uD83C\uDF2C️ of life in it—I\uD83D\uDC40 give every \uD83D\uDC9Agreen\uD83D\uDC9A plant\uD83C\uDF31 for4️⃣ \uD83C\uDF49\uD83E\uDD55\uD83C\uDF36️\uD83C\uDF44\uD83E\uDD52\uD83E\uDD5C\uD83C\uDF57\uD83C\uDF5E\uD83E\uDD56\uD83C\uDF5F\uD83E\uDD5E\uD83E\uDDC0\uD83C\uDF2D\uD83C\uDF2E\uD83E\uDD59\uD83E\uDD5Afood\uD83E\uDD58\uD83C\uDF72\uD83E\uDD57\uD83C\uDF7F\uD83C\uDF71\uD83C\uDF58\uD83C\uDF5B\uD83C\uDF5C\uD83C\uDF5D\uD83C\uDF61\uD83C\uDF66\uD83C\uDF62\uD83C\uDF68\uD83C\uDF69\uD83C\uDF82\uD83C\uDF6A\uD83C\uDF70☕\uD83E\uDD5B\uD83C\uDF7A\uD83C\uDF6C\uD83C\uDF6E\uD83E\uDD43\uD83C\uDF76.” And it was so.\n" +
            "31 God saw\uD83D\uDC41️ all that he had ⛏️⚒️made\uD83D\uDEE0️\uD83D\uDD28, and it was very good\uD83D\uDC4D\uD83D\uDC4C\uD83D\uDC4C. And there was evening\uD83C\uDF19, and there was morning☀️—the 6️⃣sixth ☀️day☀️.\n" +
            "2 Thus the heavens\uD83C\uDF24️ and the \uD83C\uDF0D\uD83C\uDF0Eearth\uD83C\uDF0F\uD83D\uDDFA️ were completed in all their vast array.\n" +
            "2 By the 7️⃣seventh ☀️day☀️ God had finished the work he had been doing; so on the 7️⃣seventh ☀️day☀️ he \uD83D\uDCA4rested\uD83D\uDCA4 from all his work. 3 Then God \uD83D\uDE07blessed\uD83D\uDE07\uD83D\uDE4F the 7️⃣seventh ☀️day☀️ and made it holy✝️\uD83D\uDE07\uD83D\uDE4F, because on it he \uD83D\uDCA4rested\uD83D\uDCA4 from all the work of⛏️⚒️ creating\uD83D\uDEE0️\uD83D\uDD28 that he had done.\n";
}
