/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.RealmSpeak;

public class RealmPoems {
	public static final String[] POEMS = {
			"Magic Realm",
			"Secrets of Magic Realm",
			"Amazon",
			"Berserker",
			"Black Knight",
			"Black Knight2",
			"Captain",
			"Druid",
			"Dwarf",
			"Elf",
			"Magician",
			"Magician2",
			"Pilgrim",
			"Pilgrim2",
			"Sorceror",
			"Sorceror2",
			"Swordsman",
			"White Knight",
			"Witch",
			"Witch2",
			"Witch King",
			"Wizard",
			"Woodsgirl",
			"Woodsgirl2"
	};
	
	public static StringBuffer getPoem(String name) {
		StringBuffer text = new StringBuffer();
		text.append("<html><body><font size=\"-1\" face=\"Helvetical, Arial, sans-serif\">");
		switch (name) {
		case "secrets of magic realm":
			text.append("The secrets in Magic Realm lay<br>");
			text.append("For some, a treasure trove to play<br>");
			text.append("But with a sharp mind<br>");
			text.append("You'll leave them behind<br>");
			text.append("And emerge victorious, in the fray.<br>");
			text.append("<br>");
			text.append("by: Psyrek");
			break;
		case "magic realm":
			text.append("In the realm of magic and mystery,<br>");
			text.append("Where danger lurks around every tree,<br>");
			text.append("We set out on our quest to find,<br>");
			text.append("Treasures lost and riches unrefined.<br>");
			text.append("<br>");
			text.append("The Amazon, with sword in hand,<br>");
			text.append("Set out to conquer and command,<br>");
			text.append("But the Enchanted Meadow proved her foe,<br>");
			text.append("With curses that she could not know.<br>");
			text.append("<br>");
			text.append("The Druid and the Pilgrim joined forces,<br>");
			text.append("To explore the wild and its resources,<br>");
			text.append("But fate would have them meet their end,<br>");
			text.append("By the axe of goblins, their journey did bend.<br>");
			text.append("<br>");
			text.append("The Witch, with power in her spell,<br>");
			text.append("Trolled and killed without a yell,<br>");
			text.append("But the curse of Wither brought her low,<br>");
			text.append("And her quest for treasure, a slow go.<br>");
			text.append("<br>");
			text.append("The Elf, with the Golden Icon in tow,<br>");
			text.append("Teamed up with the Captain, to make a show,<br>");
			text.append("But fate and fortune did not align,<br>");
			text.append("And victory for them, did not shine.<br>");
			text.append("<br>");
			text.append("The Captain, with boots that took him far,<br>");
			text.append("Sought fame and glory, like a shooting star,<br>");
			text.append("But his quest for riches, fell short in the end,<br>");
			text.append("Leaving him with tales, and not a friend.<br>");
			text.append("<br>");
			text.append("The Swordsman, with hirelings by his side,<br>");
			text.append("Set out to conquer and divide,<br>");
			text.append("But his plans were foiled, by fate's cruel hand,<br>");
			text.append("And his quest for treasure, left him damned.<br>");
			text.append("<br>");
			text.append("In the realm of magic and mystery,<br>");
			text.append("Victory is not guaranteed, it's history,<br>");
			text.append("But with each adventure, we learn and grow,<br>");
			text.append("And set out once more, to make our own tale flow.<br>");
			text.append("<br>");
			text.append("by: Psyrek");
			break;
		case "amazon":
			text.append("The Amazon on the run<br>");
			text.append("Seeking Goblins and fun<br>");
			text.append("Instead met Trolls<br>");
			text.append("And bad hide rolls<br>");
			text.append("She was too early done.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "berserker":
			text.append("Now our friendly Berserker<br>");
			text.append("Who calls Rogues his workers<br>");
			text.append("Monsters to whack<br>");
			text.append("With his big ole Axe<br>");
			text.append("Always needs one more murder.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "black knight":
			text.append("Black Knight comes swinging<br>");
			text.append("With the Company singing<br>");
			text.append("A short term of hire<br>");
			text.append("Will set a great fire<br>");
			text.append("Once they show up Drinking.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "black knight2":
			text.append("The Black Knight's reputation,<br>");
			text.append("a tale of Infamy upon denizen breath.<br>");
			text.append("The mace and crossbow his weapons<br>");
			text.append("and with them many mighty monsters death. <br>");
			text.append("Heavy Trolls, Dragons, Bats, and Spiders,<br>");
			text.append("have all met utter ruin for 27 days. <br>");
			text.append("On the 28th though, the Octopus<br>");
			text.append("pulled the Black Knight down...<br>");
			text.append("... into an unhappily ever after.<br>");
			text.append("<br>");
			text.append("by: Caesy Benn");
			break;
		case "captain":
			text.append("As our Captain begins fighting<br>");
			text.append("The Soldiers start writing<br>");
			text.append("Adventures are near<br>");
			text.append("But with great fear<br>");
			text.append("Our leader is prone to inciting.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "druid":
			text.append("The Druid alone discovers<br>");
			text.append("How quiet peace recovers<br>");
			text.append("The Dragon arrived<br>");
			text.append("The Druid surprised<br>");
			text.append("Treasures forever uncovered.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "dwarf":
			text.append("Then the Dwarf a mighty lad<br>");
			text.append("A Cave to make him glad<br>");
			text.append("Our short little hero<br>");
			text.append("Who is never a zero<br>");
			text.append("Unless you check the score pad.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "elf":
			text.append("That sneaky old trickster Elf<br>");
			text.append("Always in league with himself<br>");
			text.append("A well timed Persuade<br>");
			text.append("His game will be made<br>");
			text.append("'Till the Octopus puts him on the shelf.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "magician":
			text.append("Our own resident Magician<br>");
			text.append("Who left his job as beautician<br>");
			text.append("Spells wear off<br>");
			text.append("Onlookers scoff<br>");
			text.append("So he's here to find a new Mission.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "magician2":
			text.append("The Magician loves a good spell<br>");
			text.append("But the monsters that secretly dwell<br>");
			text.append("in each treasure site hiding<br>");
			text.append("thwart his attempts at providing<br>");
			text.append("the best trinkets and stories to tell.<br>");
			text.append("<br>");
			text.append("by: Aashiana");
			break;
		case "pilgrim":
			text.append("The Pilgrim, ever so devout<br>");
			text.append("Hear him scream hear him shout<br>");
			text.append("In search of a blessing<br>");
			text.append("Strength he was guessing<br>");
			text.append("Peace for the fourth time broke out.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "pilgrim2":
			text.append("The pilgrim casts a spell<br>");
			text.append("\"Give me strength!\" he yell<br>");
			text.append("\"I'll give you health\"  a voice shouts<br>");
			text.append("The pilgrim  frowns as his wish was not  granted<br>");
			text.append("Strength is what he wanted.<br>");
			text.append("<br>");
			text.append("by: Moistyclams");
			break;
		case "sorceror":
			text.append("A Sorceror mighty and fair<br>");
			text.append("Melting into Mist in the air<br>");
			text.append("A Firey Blast<br>");
			text.append("Lands square at last<br>");
			text.append("Anything to forget that hair.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "sorceror2":
			text.append("The Sorceror does not have a single care<br>");
			text.append("because he can just Melt to Mist.<br>");
			text.append("Transforming Tremendous armored monsters into frogs, <br>");
			text.append("even unfriendly or enemy Denizens get the gist.<br>");
			text.append("He acts all helpless wandering around in the ruins<br>");
			text.append("hoping hordes of goblins will arrive at last.<br>");
			text.append("If he doesn't fail his hide roll and alerts a magic chit<br>");
			text.append("the Sorceror will soon be having a Fiery Blast.<br>");
			text.append("<br>");
			text.append("by: Caesy Benn");
			break;
		case "swordsman":
			text.append("Nimble and lithe this rascal<br>");
			text.append("the Swordsman, ever the jackal<br>");
			text.append("He'll get in your wayt<br>");
			text.append("And then run away<br>");
			text.append("To fight the Bats in the Castle.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "white knight":
			text.append("Next our mighty White Knight<br>");
			text.append("Whom you do not want to fight<br>");
			text.append("Oh! What is that?<br>");
			text.append("Here comes a Bat<br>");
			text.append("Our warrior goes toward the light.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "witch":
			text.append("Our little old lady, the Witch<br>");
			text.append("Kind until she gets that itch<br>");
			text.append("Absorbs a T Troll<br>");
			text.append("She's on a roll<br>");
			text.append("Before you know it, she's Rich.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "witch2":
			text.append("There once was a powerful Witch<br>");
			text.append("Who had a familiar named Stich<br>");
			text.append("It went to each clearing<br>");
			text.append("And did some fine Peering<br>");
			text.append("And made them all both quite rich.<br>");
			text.append("<br>");
			text.append("by: CthulhuKid");
			break;
		case "witch king":
			text.append("Which way the Witch King<br>");
			text.append("How to make the spells sing<br>");
			text.append("The Book of Lore<br>");
			text.append("Like days of yore<br>");
			text.append("Delivers the ultimate zing.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "wizard":
			text.append("A Wizard who knows every path<br>");
			text.append("Unleashing his mighty wrath<br>");
			text.append("All round the Realm<br>");
			text.append("Friends to overwhelm<br>");
			text.append("This hiker surely needs a bath.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "woodsgirl":
			text.append("The Woods Girl deadly and quick<br>");
			text.append("Seeking a Monster to stick<br>");
			text.append("An arrow let fly<br>");
			text.append("A six on her die<br>");
			text.append("Running away is her trick.<br>");
			text.append("<br>");
			text.append("by: Quantum Jack");
			break;
		case "woodsgirl2":
			text.append("The Woodsgirl leaves home as a child<br>");
			text.append("Journeys through the forests so wild<br>");
			text.append("Her aim is quite deadly<br>");
			text.append("Her fame growing steadily<br>");
			text.append("She has all the natives beguiled.<br>");
			text.append("<br>");
			text.append("by: Aashiana");
			break;
		default:
			return null;
		}
		text.append("</font></body></html>");
		return text;
	}
}