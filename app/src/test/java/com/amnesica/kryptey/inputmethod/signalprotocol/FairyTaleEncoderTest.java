package com.amnesica.kryptey.inputmethod.signalprotocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.encoding.FairyTaleEncoder;

import org.junit.Test;

import java.io.IOException;

public class FairyTaleEncoderTest {
  static final String TAG = FairyTaleEncoderTest.class.getSimpleName();

  // hint: dummy message, not real PreKeyMessage!
  private final String message = "{\n" +
      "  \"ciphertextMessage\" : null,\n" +
      "  \"ciphertextType\" : 0,\n" +
      "  \"deviceId\" : 2860,\n" +
      "  \"preKeyResponse\" : {\n" +
      "    \"devices\" : [ {\n" +
      "      \"deviceId\" : 2860,\n" +
      "      \"preKey\" : {\n" +
      "        \"keyId\" : 187,\n" +
      "        \"publicKey\" : \"BWYx2LP/2DSzm/zJtDazzqcamaEVo0kyU+rEzu73jFvJM\"\n" +
      "      },\n" +
      "      \"registrationId\" : 16268,\n" +
      "      \"signedPreKey\" : {\n" +
      "        \"keyId\" : 60,\n" +
      "        \"publicKey\" : \"BUiB07r7Qr0YnnESeaNWhdQq1SshdjdnVohxHfyWA4JkNw4d\",\n" +
      "        \"signature\" : \"K6GButcueEdIYifKyOhsrXxiNUsHsEOPTVFV96efMfAkO0uE0zH5CrpZU25nhy3lUgGEdYFgQIT29JyuXg3IFGhQ\"\n" +
      "      }\n" +
      "    } ],\n" +
      "    \"identityKey\" : {\n" +
      "      \"publicKey\" : \"BaUr2CmXaHgL0DUvmyHfQPYUdhs3lwW5+5ieuSskDMazh9\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"signalProtocolAddressName\" : \"f50d2f3f-ca11-4985-ac31-904f80cf4a07\",\n" +
      "  \"timestamp\" : 1674142143915\n" +
      "} ";

  private final String messageDeSimplified = "{\"ciphertextMessage\":null,\"ciphertextType\":0,\"deviceId\":2860,\"preKeyResponse\":{\"devices\":[{\"deviceId\":2860,\"preKey\":{\"keyId\":187,\"publicKey\":\"BWYx2LP/2DSzm/zJtDazzqcamaEVo0kyU+rEzu73jFvJM\"},\"registrationId\":16268,\"signedPreKey\":{\"keyId\":60,\"publicKey\":\"BUiB07r7Qr0YnnESeaNWhdQq1SshdjdnVohxHfyWA4JkNw4d\",\"signature\":\"K6GButcueEdIYifKyOhsrXxiNUsHsEOPTVFV96efMfAkO0uE0zH5CrpZU25nhy3lUgGEdYFgQIT29JyuXg3IFGhQ\"}}],\"identityKey\":{\"publicKey\":\"BaUr2CmXaHgL0DUvmyHfQPYUdhs3lwW5+5ieuSskDMazh9\"}},\"signalProtocolAddressName\":\"f50d2f3f-ca11-4985-ac31-904f80cf4a07\",\"timestamp\":1674142143915}";

  public static String rapunzelText = "There were once a man and a woman who had long in vain\n" +
      "wished for a child.  At length the woman hoped that God\n" +
      "was about to grant her desire.  These people had a little\n" +
      "window at the back of their house from which a splendid garden\n" +
      "could be seen, which was full of the most beautiful flowers and\n" +
      "herbs.  It was, however, surrounded by a high wall, and no one\n" +
      "dared to go into it because it belonged to an enchantress, who had\n" +
      "great power and was dreaded by all the world.  One day the woman\n" +
      "was standing by this window and looking down into the garden,\n" +
      "when she saw a bed which was planted with the most beautiful\n" +
      "rampion - rapunzel, and it looked so fresh and green that she\n" +
      "longed for it, and had the greatest desire to eat some.  This desire\n" +
      "increased every day, and as she knew that she could not get any\n" +
      "of it, she quite pined away, and began to look pale and miserable.\n" +
      "Then her husband was alarmed, and asked, what ails you, dear\n" +
      "wife.  Ah, she replied, if I can\'t eat some of the rampion, which\n" +
      "is in the garden behind our house, I shall die.  The man, who loved\n" +
      "her, thought, sooner than let your wife die, bring her some of\n" +
      "the rampion yourself, let it cost what it will.  At twilight, he\n" +
      "clambered down over the wall into the garden of the enchantress,\n" +
      "hastily clutched a handful of rampion, and took it to his wife.  She\n" +
      "at once made herself a salad of it, and ate it greedily.  It tasted\n" +
      "so good to her - so very good, that the next day she longed for it\n" +
      "three times as much as before.  If he was to have any rest, her\n" +
      "husband must once more descend into the garden.  In the gloom of\n" +
      "evening, therefore, he let himself down again.  But when he had\n" +
      "clambered down the wall he was terribly afraid, for he saw the\n" +
      "enchantress standing before him.  How can you dare, said she with\n" +
      "angry look, descend into my garden and steal my rampion like a\n" +
      "thief.  You shall suffer for it.  Ah, answered he, let mercy take\n" +
      "the place of justice, I only made up my mind to do it out of\n" +
      "necessity.  My wife saw your rampion from the window, and felt such\n" +
      "a longing for it that she would have died if she had not got some\n" +
      "to eat.  Then the enchantress allowed her anger to be softened, and\n" +
      "said to him, if the case be as you say, I will allow you to take\n" +
      "away with you as much rampion as you will, only I make one\n" +
      "condition, you must give me the child which your wife will bring\n" +
      "into the world.  It shall be well treated, and I will care for it\n" +
      "like a mother.  The man in his terror consented to everything, and\n" +
      "when the woman was brought to bed, the enchantress appeared at once,\n" +
      "gave the child the name of rapunzel, and took it away with her.\n" +
      "Rapunzel grew into the most beautiful child under the sun.\n" +
      "When she was twelve years old, the enchantress shut her into a\n" +
      "tower, which lay in a forest, and had neither stairs nor door, but\n" +
      "quite at the top was a little window.  When the enchantress\n" +
      "wanted to go in, she placed herself beneath it and cried,\n" +
      "     rapunzel, rapunzel,\n" +
      "     let down your hair to me.\n" +
      "Rapunzel had magnificent long hair, fine as spun gold, and when\n" +
      "she heard the voice of the enchantress she unfastened her braided\n" +
      "tresses, wound them round one of the hooks of the window above,\n" +
      "and then the hair fell twenty ells down, and the enchantress climbed\n" +
      "up by it.\n" +
      "After a year or two, it came to pass that the king\'s son rode\n" +
      "through the forest and passed by the tower.  Then he heard a song,\n" +
      "which was so charming that he stood still and listened.  This was\n" +
      "rapunzel, who in her solitude passed her time in letting her sweet\n" +
      "voice resound.  The king\'s son wanted to climb up to her, and\n" +
      "looked for the door of the tower, but none was to be found.  He\n" +
      "rode home, but the singing had so deeply touched his heart, that\n" +
      "every day he went out into the forest and listened to it.  Once when\n" +
      "he was thus standing behind a tree, he saw that an enchantress\n" +
      "came there, and he heard how she cried,\n" +
      "     rapunzel, rapunzel,\n" +
      "     let down your hair.\n" +
      "Then rapunzel let down the braids of her hair, and the\n" +
      "enchantress climbed up to her.  If that is the ladder by which one\n" +
      "mounts, I too will try my fortune, said he, and the next day when\n" +
      "it began to grow dark, he went to the tower and cried,\n" +
      "     rapunzel, rapunzel,\n" +
      "     let down your hair.\n" +
      "Immediately the hair fell down and the king\'s son climbed up.\n" +
      "At first rapunzel was terribly frightened when a man, such as\n" +
      "her eyes had never yet beheld, came to her.  But the king\'s son\n" +
      "began to talk to her quite like a friend, and told her that his\n" +
      "heart had been so stirred that it had let him have no rest, and he\n" +
      "had been forced to see her.  Then rapunzel lost her fear, and when\n" +
      "he asked her if she would take him for her husband, and she saw that\n" +
      "he was young and handsome, she thought, he will love me more than\n" +
      "old dame gothel does.  And she said yes, and laid her hand in his.\n" +
      "She said, I will willingly go away with you, but I do not know\n" +
      "how to get down.  Bring with you a skein of silk every time that\n" +
      "you come, and I will weave a ladder with it, and when that is ready\n" +
      "I will descend, and you will take me on your horse.  They agreed\n" +
      "that until that time he should come to her every evening, for the\n" +
      "old woman came by day.  The enchantress remarked nothing of\n" +
      "this, until once rapunzel said to her, tell me, dame gothel, how\n" +
      "it happens that you are so much heavier for me to draw up than\n" +
      "the young king\'s son - he is with me in a moment.  Ah. You\n" +
      "wicked child, cried the enchantress.  What do I hear you say.  I\n" +
      "thought I had separated you from all the world, and yet you have\n" +
      "deceived me.  In her anger she clutched rapunzel\'s beautiful\n" +
      "tresses, wrapped them twice round her left hand, seized a pair of\n" +
      "scissors with the right, and snip, snap, they were cut off, and the\n" +
      "lovely braids lay on the ground.  And she was so pitiless that she\n" +
      "took poor rapunzel into a desert where she had to live in great\n" +
      "grief and misery.\n" +
      "On the same day that she cast out rapunzel, however, the\n" +
      "enchantress fastened the braids of hair, which she had cut off, to\n" +
      "the hook of the window, and when the king\'s son came and cried,\n" +
      "     rapunzel, rapunzel,\n" +
      "     let down your hair,\n" +
      "she let the hair down.  The king\'s son ascended, but instead of\n" +
      "finding his dearest rapunzel, he found the enchantress, who gazed\n" +
      "at him with wicked and venomous looks.  Aha, she cried mockingly,\n" +
      "you would fetch your dearest, but the beautiful bird sits\n" +
      "no longer singing in the nest.  The cat has got it, and will scratch\n" +
      "out your eyes as well.  Rapunzel is lost to you.  You will never see\n" +
      "her again.  The king\'s son was beside himself with pain, and in\n" +
      "his despair he leapt down from the tower.  He escaped with his life,\n" +
      "but the thorns into which he fell pierced his eyes.  Then he\n" +
      "wandered quite blind about the forest, ate nothing but roots and\n" +
      "berries, and did naught but lament and weep over the loss of his\n" +
      "dearest wife.  Thus he roamed about in misery for some years, and at\n" +
      "length came to the desert where rapunzel, with the twins to which\n" +
      "she had given birth, a boy and a girl, lived in wretchedness.  He\n" +
      "heard a voice, and it seemed so familiar to him that he went towards\n" +
      "it, and when he approached, rapunzel knew him and fell on his neck\n" +
      "and wept.  Two of her tears wetted his eyes and they grew clear\n" +
      "again, and he could see with them as before.  He led her to his\n" +
      "kingdom where he was joyfully received, and they lived for a long\n" +
      "time afterwards, happy and contented.";

  public static String cinderellaText = "The wife of a rich man fell sick, and as she felt that her end\n" +
      "was drawing near, she called her only daughter to her bedside and\n" +
      "said, dear child, be good and pious, and then the\n" +
      "good God will always protect you, and I will look down on you\n" +
      "from heaven and be near you.  Thereupon she closed her eyes and\n" +
      "departed.  Every day the maiden went out to her mother\'s grave,\n" +
      "and wept, and she remained pious and good.  When winter came\n" +
      "the snow spread a white sheet over the grave, and by the time the\n" +
      "spring sun had drawn it off again, the man had taken another wife.\n" +
      "The woman had brought with her into the house two daughters,\n" +
      "who were beautiful and fair of face, but vile and black of heart.\n" +
      "Now began a bad time for the poor step-child.  Is the stupid goose\n" +
      "to sit in the parlor with us, they said.  He who wants to eat bread\n" +
      "must earn it.  Out with the kitchen-wench.  They took her pretty\n" +
      "clothes away from her, put an old grey bedgown on her, and gave\n" +
      "her wooden shoes.  Just look at the proud princess, how decked\n" +
      "out she is, they cried, and laughed, and led her into the kitchen.\n" +
      "There she had to do hard work from morning till night, get up\n" +
      "before daybreak, carry water, light fires, cook and wash.  Besides\n" +
      "this, the sisters did her every imaginable injury - they mocked her\n" +
      "and emptied her peas and lentils into the ashes, so that she was\n" +
      "forced to sit and pick them out again.  In the evening when she had\n" +
      "worked till she was weary she had no bed to go to, but had to sleep\n" +
      "by the hearth in the cinders.  And as on that account she always\n" +
      "looked dusty and dirty, they called her cinderella.\n" +
      "It happened that the father was once going to the fair, and he\n" +
      "asked his two step-daughters what he should bring back for them.\n" +
      "Beautiful dresses, said one, pearls and jewels, said the second.\n" +
      "And you, cinderella, said he, what will you have.  Father\n" +
      "break off for me the first branch which knocks against your hat on\n" +
      "your way home.  So he bought beautiful dresses, pearls and jewels\n" +
      "for his two step-daughters, and on his way home, as he was riding\n" +
      "through a green thicket, a hazel twig brushed against him and\n" +
      "knocked off his hat.  Then he broke off the branch and took it with\n" +
      "him.  When he reached home he gave his step-daughters the things\n" +
      "which they had wished for, and to cinderella he gave the branch\n" +
      "from the hazel-bush.  Cinderella thanked him, went to her mother\'s\n" +
      "grave and planted the branch on it, and wept so much that the tears\n" +
      "fell down on it and watered it.  And it grew and became a handsome\n" +
      "tree. Thrice a day cinderella went and sat beneath it, and wept and\n" +
      "prayed, and a little white bird always came on the tree, and if\n" +
      "cinderella expressed a wish, the bird threw down to her what she\n" +
      "had wished for.\n" +
      "It happened, however, that the king gave orders for a festival\n" +
      "which was to last three days, and to which all the beautiful young\n" +
      "girls in the country were invited, in order that his son might choose\n" +
      "himself a bride.  When the two step-sisters heard that they too were\n" +
      "to appear among the number, they were delighted, called cinderella\n" +
      "and said, comb our hair for us, brush our shoes and fasten our\n" +
      "buckles, for we are going to the wedding at the king\'s palace.\n" +
      "Cinderella obeyed, but wept, because she too would have liked to\n" +
      "go with them to the dance, and begged her step-mother to allow\n" +
      "her to do so.  You go, cinderella, said she, covered in dust and\n" +
      "dirt as you are, and would go to the festival.  You have no clothes\n" +
      "and shoes, and yet would dance.  As, however, cinderella went on\n" +
      "asking, the step-mother said at last, I have emptied a dish of\n" +
      "lentils into the ashes for you, if you have picked them out again in\n" +
      "two hours, you shall go with us.  The maiden went through the\n" +
      "back-door into the garden, and called, you tame pigeons, you\n" +
      "turtle-doves, and all you birds beneath the sky, come and help me\n" +
      "to pick\n" +
      "     the good into the pot,\n" +
      "     the bad into the crop.\n" +
      "Then two white pigeons came in by the kitchen window, and\n" +
      "afterwards the turtle-doves, and at last all the birds beneath the\n" +
      "sky, came whirring and crowding in, and alighted amongst the ashes.\n" +
      "And the pigeons nodded with their heads and began pick, pick,\n" +
      "pick, pick, and the rest began also pick, pick, pick, pick, and\n" +
      "gathered all the good grains into the dish.  Hardly had one hour\n" +
      "passed before they had finished, and all flew out again.  Then the\n" +
      "girl took the dish to her step-mother, and was glad, and believed\n" +
      "that now she would be allowed to go with them to the festival.\n" +
      "But the step-mother said, no, cinderella, you have no clothes and\n" +
      "you can not dance.  You would only be laughed at.  And as\n" +
      "cinderella wept at this, the step-mother said, if you can pick two\n" +
      "dishes of lentils out of the ashes for me in one hour, you shall go\n" +
      "with us.  And she thought to herself, that she most certainly\n" +
      "cannot do again.  When the step-mother had emptied the two\n" +
      "dishes of lentils amongst the ashes, the maiden went through the\n" +
      "back-door into the garden and cried, you tame pigeons, you\n" +
      "turtle-doves, and all you birds beneath the sky, come and help me\n" +
      "to pick\n" +
      "     the good into the pot,\n" +
      "     the bad into the crop.\n" +
      "Then two white pigeons came in by the kitchen-window, and\n" +
      "afterwards the turtle-doves, and at length all the birds beneath the\n" +
      "sky, came whirring and crowding in, and alighted amongst the\n" +
      "ashes.  And the doves nodded with their heads and began pick,\n" +
      "pick, pick, pick, and the others began also pick, pick, pick, pick,\n" +
      "and gathered all the good seeds into the dishes, and before half an\n" +
      "hour was over they had already finished, and all flew out again.\n" +
      "Then the maiden was delighted, and believed that she might now go\n" +
      "with them to the wedding.  But the step-mother said, all this will\n" +
      "not help.  You cannot go with us, for you have no clothes and can\n" +
      "not dance.  We should be ashamed of you.  On this she turned her\n" +
      "back on cinderella, and hurried away with her two proud daughters.\n" +
      "As no one was now at home, cinderella went to her mother\'s\n" +
      "grave beneath the hazel-tree, and cried -\n" +
      "     shiver and quiver, little tree,\n" +
      "     silver and gold throw down over me.\n" +
      "Then the bird threw a gold and silver dress down to her, and\n" +
      "slippers embroidered with silk and silver.  She put on the dress\n" +
      "with all speed, and went to the wedding.  Her step-sisters and the\n" +
      "step-mother however did not know her, and thought she must be a\n" +
      "foreign princess, for she looked so beautiful in the golden dress.\n" +
      "They never once thought of cinderella, and believed that she was\n" +
      "sitting at home in the dirt, picking lentils out of the ashes.  The\n" +
      "prince approached her, took her by the hand and danced with her.\n" +
      "He would dance with no other maiden, and never let loose of her\n" +
      "hand, and if any one else came to invite her, he said, this is my\n" +
      "partner.\n" +
      "She danced till it was evening, and then she wanted to go home.\n" +
      "But the king\'s son said, I will go with you and bear you company,\n" +
      "for he wished to see to whom the beautiful maiden belonged.\n" +
      "She escaped from him, however, and sprang into the\n" +
      "pigeon-house.  The king\'s son waited until her father came, and\n" +
      "then he told him that the unknown maiden had leapt into the\n" +
      "pigeon-house.  The old man thought, can it be cinderella.  And\n" +
      "they had to bring him an axe and a pickaxe that he might hew\n" +
      "the pigeon-house to pieces, but no one was inside it.  And when they\n" +
      "got home cinderella lay in her dirty clothes among the ashes, and\n" +
      "a dim little oil-lamp was burning on the mantle-piece, for\n" +
      "cinderella had jumped quickly down from the back of the pigeon-house\n" +
      "and had run to the little hazel-tree, and there she had taken off\n" +
      "her beautiful clothes and laid them on the grave, and the bird had\n" +
      "taken them away again, and then she had seated herself in the\n" +
      "kitchen amongst the ashes in her grey gown.\n" +
      "Next day when the festival began afresh, and her parents and\n" +
      "the step-sisters had gone once more, cinderella went to the\n" +
      "hazel-tree and said -\n" +
      "     shiver and quiver, my little tree,\n" +
      "     silver and gold throw down over me.\n" +
      "Then the bird threw down a much more beautiful dress than on\n" +
      "the preceding day. And when cinderella appeared at the wedding\n" +
      "in this dress, every one was astonished at her beauty.  The king\'s\n" +
      "son had waited until she came, and instantly took her by the hand\n" +
      "and danced with no one but her.  When others came and invited\n" +
      "her, he said, this is my partner.  When evening came she wished\n" +
      "to leave, and the king\'s son followed her and wanted to see into\n" +
      "which house she went.  But she sprang away from him, and into\n" +
      "the garden behind the house.  Therein stood a beautiful tall tree on\n" +
      "which hung the most magnificent pears.  She clambered so nimbly\n" +
      "between the branches like a squirrel that the king\'s son did not\n" +
      "know where she was gone.  He waited until her father came, and\n" +
      "said to him, the unknown maiden has escaped from me, and I\n" +
      "believe she has climbed up the pear-tree.  The father thought,\n" +
      "can it be cinderella.  And had an axe brought and cut the\n" +
      "tree down, but no one was on it.  And when they got into the\n" +
      "kitchen, cinderella lay there among the ashes, as usual, for she\n" +
      "had jumped down on the other side of the tree, had taken the\n" +
      "beautiful dress to the bird on the little hazel-tree, and put on her\n" +
      "grey gown.\n" +
      "On the third day, when the parents and sisters had gone away,\n" +
      "cinderella went once more to her mother\'s grave and said to the\n" +
      "little tree -\n" +
      "     shiver and quiver, my little tree,\n" +
      "     silver and gold throw down over me.\n" +
      "And now the bird threw down to her a dress which was more\n" +
      "splendid and magnificent than any she had yet had, and the\n" +
      "slippers were golden.  And when she went to the festival in the\n" +
      "dress, no one knew how to speak for astonishment.  The king\'s son\n" +
      "danced with her only, and if any one invited her to dance, he said\n" +
      "this is my partner.\n" +
      "When evening came, cinderella wished to leave, and the king\'s\n" +
      "son was anxious to go with her, but she escaped from him so quickly\n" +
      "that he could not follow her.  The king\'s son, however, had\n" +
      "employed a ruse, and had caused the whole staircase to be smeared\n" +
      "with pitch, and there, when she ran down, had the maiden\'s left\n" +
      "slipper remained stuck.  The king\'s son picked it up, and it was\n" +
      "small and dainty, and all golden.  Next morning, he went with it to\n" +
      "the father, and said to him, no one shall be my wife but she whose\n" +
      "foot this golden slipper fits.  Then were the two sisters glad,\n" +
      "for they had pretty feet.  The eldest went with the shoe into her\n" +
      "room and wanted to try it on, and her mother stood by.  But she\n" +
      "could not get her big toe into it, and the shoe was too small for\n" +
      "her.  Then her mother gave her a knife and said, cut the toe off,\n" +
      "when you are queen you will have no more need to go on foot.  The\n" +
      "maiden cut the toe off, forced the foot into the shoe, swallowed\n" +
      "the pain, and went out to the king\'s son.  Then he took her on his\n" +
      "his horse as his bride and rode away with her.  They were\n" +
      "obliged, however, to pass the grave, and there, on the hazel-tree,\n" +
      "sat the two pigeons and cried -\n" +
      "     turn and peep, turn and peep,\n" +
      "     there\'s blood within the shoe,\n" +
      "     the shoe it is too small for her,\n" +
      "     the true bride waits for you.\n" +
      "Then he looked at her foot and saw how the blood was trickling\n" +
      "from it.  He turned his horse round and took the false bride\n" +
      "home again, and said she was not the true one, and that the\n" +
      "other sister was to put the shoe on.  Then this one went into her\n" +
      "chamber and got her toes safely into the shoe, but her heel was\n" +
      "too large.  So her mother gave her a knife and said,  cut a bit\n" +
      "off your heel, when you are queen you will have no more need\n" +
      "to go on foot.  The maiden cut a bit off her heel, forced\n" +
      "her foot into the shoe, swallowed the pain, and went out to the\n" +
      "king\'s son.  He took her on his horse as his bride, and rode away\n" +
      "with her, but when they passed by the hazel-tree, the two pigeons\n" +
      "sat on it and cried -\n" +
      "     turn and peep, turn and peep,\n" +
      "     there\'s blood within the shoe,\n" +
      "     the shoe it is too small for her,\n" +
      "     the true bride waits for you.\n" +
      "He looked down at her foot and saw how the blood was running\n" +
      "out of her shoe, and how it had stained her white stocking quite\n" +
      "red.  Then he turned his horse and took the false bride home\n" +
      "again.  This also is not the right one, said he, have you no\n" +
      "other daughter.  No, said the man, there is still a little\n" +
      "stunted kitchen-wench which my late wife left behind her, but\n" +
      "she cannot possibly be the bride.  The king\'s son said he was\n" +
      "to send her up to him, but the mother answered, oh, no, she is\n" +
      "much too dirty, she cannot show herself.  But he absolutely\n" +
      "insisted on it, and cinderella had to be called.  She first\n" +
      "washed her hands and face clean, and then went and bowed down\n" +
      "before the king\'s son, who gave her the golden shoe.  Then she\n" +
      "seated herself on a stool, drew her foot out of the heavy\n" +
      "wooden shoe, and put it into the slipper, which fitted like a\n" +
      "glove.  And when she rose up and the king\'s son looked at her\n" +
      "face he recognized the beautiful maiden who had danced with\n" +
      "him and cried, that is the true bride.  The step-mother and\n" +
      "the two sisters were horrified and became pale with rage, he,\n" +
      "however, took cinderella on his horse and rode away with her.  As\n" +
      "they passed by the hazel-tree, the two white doves cried -\n" +
      "     turn and peep, turn and peep,\n" +
      "     no blood is in the shoe,\n" +
      "     the shoe is not too small for her,\n" +
      "     the true bride rides with you,\n" +
      "and when they had cried that, the two came flying down and\n" +
      "placed themselves on cinderella\'s shoulders, one on the right,\n" +
      "the other on the left, and remained sitting there.\n" +
      "When the wedding with the king\'s son was to be celebrated, the\n" +
      "two false sisters came and wanted to get into favor with\n" +
      "cinderella and share her good fortune.  When the betrothed\n" +
      "couple went to church, the elder was at the right side and the\n" +
      "younger at the left, and the pigeons pecked out one eye from\n" +
      "each of them.  Afterwards as they came back the elder was at\n" +
      "the left, and the younger at the right, and then the pigeons\n" +
      "pecked out the other eye from each.  And thus, for their\n" +
      "wickedness and falsehood, they were punished with blindness\n" +
      "all their days.";

  @Test
  public void initSentenceMapTest() {
    Log.d(TAG, "------------ initSentenceMapTest: ------------");
    FairyTaleEncoder.initForTest(rapunzelText, cinderellaText);
    assertNotNull(FairyTaleEncoder.mSentencesMap);
  }

  @Test
  public void encodeDecodeTest() throws IOException {
    Log.d(TAG, "------------ encodeDecodeTest: ------------");
    FairyTaleEncoder.initForTest(rapunzelText, cinderellaText);

    // encode
    final String encodedMessage = FairyTaleEncoder.encode(message, null);
    assertNotNull(encodedMessage);
    Log.d(TAG, "encodedMessage: " + encodedMessage);
    Log.d(TAG, "encodedMessage (length): " + encodedMessage.length());

    // decode
    String decodedMessage = FairyTaleEncoder.decode(encodedMessage);
    assertNotNull(decodedMessage);
    assertEquals(messageDeSimplified, decodedMessage);
  }
}
