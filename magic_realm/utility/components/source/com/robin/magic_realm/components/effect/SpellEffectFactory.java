package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SpellUtility;

public class SpellEffectFactory {	
	public static ISpellEffect[] create(String spellName,String alternativeSpellEffect){
		switch(spellName.toLowerCase()){
			case "absorb essence":return new ISpellEffect[]{new TransmorphEffect("target")};
			case "animate": return new ISpellEffect[]{new AnimateEffect()};
			case "ask demon": return new ISpellEffect[]{new AskDemonEffect()};
		
			case "bewilder": return new ISpellEffect[]{new ApplyClearingEffect(Constants.BEWILDERED)};
			case "blazing light": return new ISpellEffect[]{new ExtraCavePhaseEffect()};
			case "blazing light x": return new ISpellEffect[]{new ApplyNamedEffect(Constants.TORCH_BEARER)};
			case "body double": return new ISpellEffect[]{new ApplyNamedEffect(Constants.BODY_DOUBLE)};
		
			case "blend into background": return new ISpellEffect[]{new ExtraActionEffect("H")};
			case "blend into background x": return new ISpellEffect[]{new ExtraActionEffect("H")};
			case "blunting": return new ISpellEffect[]{new ApplyClearingEffect(Constants.BLUNTED)};
			case "blur": return new ISpellEffect[]{new FinalChitSpeedEffect()};
		
			case "broomstick": return new ISpellEffect[]{new FlyChitEffect()};
		
			case "control bats": return new ISpellEffect[]{new ControlEffect()};
			case "control dragon": return new ISpellEffect[]{new ControlEffect()};
			case "curse": return new ISpellEffect[]{new CurseEffect()};
	
			case "exorcise": return new ISpellEffect[]{new ExorciseEffect()};
		
			case "deal with goblins": return new ISpellEffect[]{new PacifyEffect(0)};
			case "disguise": return new ISpellEffect[]{new PacifyEffect(0)};
			case "disjunction": return new ISpellEffect[]{new ApplyNamedEffect(Constants.NO_WEATHER_FATIGUE),new ApplyNamedEffect(Constants.NO_TERRAIN_HARM)};
		
			case "dissolve spell": return new ISpellEffect[]{new CancelEffect()};
			case "divine might": return new ISpellEffect[]{new ApplyNamedEffect(Constants.STRONG_MF)};
			case "divine shield": return new ISpellEffect[]{new ApplyNamedEffect(Constants.ADDS_ARMOR)};
		
			case "elemental power": return new ISpellEffect[]{new ForcedEnchantEffect()};
			case "elemental spirit": return new ISpellEffect[]{new ChitChangeEffect()};
			case "elven grace": return new ISpellEffect[]{new MoveSpeedChangeEffect()};
		
			case "enchant artifact": return new ISpellEffect[]{new EnchantEffect()};
			case "eternal servant": return new ISpellEffect[]{new NoWeightEffect()};
		
			case "fae guard": return new ISpellEffect[]{new SummonFairyEffect()};
			case "faerie lights": return new ISpellEffect[]{new ChitChangeEffect()};
			case "filcher": return new ISpellEffect[]{new FilcherEffect()};
			
			case "flame staff": return new ISpellEffect[]{new AddSharpnessEffect(2)};
			
			case "flying carpet spell": return new ISpellEffect[]{new FlyStrengthEffect()};
			
			case "fog": return new ISpellEffect[]{new ApplyNamedEffect(Constants.SP_NO_PEER)};
		
			case "gravity": return new ISpellEffect[]{new ApplyClearingEffect(Constants.HEAVIED)};
			case "guide spider or octopus": return new ISpellEffect[]{new ControlEffect()};
		
			case "heal": return new ISpellEffect[]{new HealChitEffect()};
			case "hop": return new ISpellEffect[]{new TeleportEffect(SpellUtility.TeleportType.RandomClearing)};
			case "hurricane winds": return new ISpellEffect[]{new HurricaneWindsEffect()};
			case "hypnotize": return new ISpellEffect[]{new ControlEffect()};
		
			case "levitate": return new ISpellEffect[]{new NoWeightEffect()};
			case "lost": return new ISpellEffect[]{new ApplyNamedEffect(Constants.SP_MOVE_IS_RANDOM), new MazeCheckEffect()};
			case "mage guard": return new ISpellEffect[]{new MageGuardEffect()};
			case "make whole": return new ISpellEffect[]{new MakeWholeEffect()};
			case "melt into mist": return new ISpellEffect[]{new NullifyEffect(),new DisengageEffect(), new TransmorphEffect("mist")};
			case "miracle": return new ISpellEffect[]{new MiracleEffect()};
			case "open gate": return new ISpellEffect[]{new TeleportEffect(SpellUtility.TeleportType.KnownGate)};
		
			case "peace": return new ISpellEffect[]{new PeaceEffect()};
			case "peace with nature": return new ISpellEffect[]{new ApplyNamedEffect(Constants.PEACE_WITH_NATURE)};
		
			case "pentangle": return new ISpellEffect[]{new NullifyEffect()};
			case "persuade": return new ISpellEffect[]{new PacifyEffect(1)};
		
			case "phantasm": return new ISpellEffect[]{new PhantasmEffect()};
			case "poison": return new ISpellEffect[]{new AddSharpnessEffect(1),new ApplyNamedEffect(Constants.POISON)};
			case "power of the pit": return new ISpellEffect[]{new PowerPitEffect()};
		
			case "prayer": return new ISpellEffect[]{new ExtraActionEffect("R")};
			case "premonition": return new ISpellEffect[]{new ApplyNamedEffect(Constants.CHOOSE_TURN)};
			case "prophecy": return new ISpellEffect[]{new ApplyNamedEffect(Constants.DAYTIME_ACTIONS)};
			case "protection from magic": return new ISpellEffect[]{new PhaseChitEffect()};

			case "raise dead": return new ISpellEffect[]{new SummonEffect(SpellUtility.SummonType.undead.toString())};
			case "remedy": return new ISpellEffect[]{new CancelEffect()};
			case "repair armor": return new ISpellEffect[]{new RepairEffect()};
			case "reverse power": return new ISpellEffect[]{new ColorModEffect()};
		
			case "see/change weather": return new ISpellEffect[]{new SeeChangeWeatherEffect()};
			case "see hidden signs": return new ISpellEffect[]{new ExtraActionEffect("S")};
			case "see hidden signs x": return new ISpellEffect[]{new ExtraActionEffect("S")};
			case "send": return new ISpellEffect[]{new ControlEffect()};
		
			case "sense danger": return new ISpellEffect[]{new ExtraActionEffect("A")};
			case "serpent tongue": return new ISpellEffect[]{new ControlEffect()};
			case "shrink": return new ISpellEffect[]{new ApplyNamedEffect(Constants.SHRINK)};
		
			case "slow monster": return new ISpellEffect[]{new ApplyNamedEffect(Constants.SLOWED)};
			case "small blessing": return new ISpellEffect[]{new SmallBlessingEffect()};
			case "sparkle": return new ISpellEffect[]{new UnassignEffect()};
		
			case "spirit guide": return new ISpellEffect[]{new ApplyNamedEffect(Constants.SPIRIT_GUIDE)};
			case "staff to snake": return new ISpellEffect[]{new ChangeToCompanionEffect()};
			case "stone gaze": return new ISpellEffect[]{new PetrifyEffect()};
		
			case "summon aid": return new ISpellEffect[]{new SummonAidEffect()};
			case "summon animal": return new ISpellEffect[]{new SummonEffect(SpellUtility.SummonType.animal.toString())};
			case "summon elemental": return new ISpellEffect[]{new SummonEffect(SpellUtility.SummonType.elemental.toString())};
			case "sword song": return new ISpellEffect[]{new ApplyNamedEffect(Constants.ALERTED_WEAPON), new AlertWeaponEffect()};
		
			case "talk to wise bird": return new ISpellEffect[]{new InstantPeerEffect()};
			case "teleport": return new ISpellEffect[]{new TeleportEffect(SpellUtility.TeleportType.ChooseTileTwo)};
		
			case "transform": return new ISpellEffect[]{new TransmorphEffect("roll")};
		
			case "unleash power": return new ISpellEffect[]{new ActionChangeEffect()};
		
			case "vale walker": return new ISpellEffect[]{new ApplyNamedEffect(Constants.VALE_WALKER)};
			case "violent storm": return new ISpellEffect[]{new ViolentStormEffect()};
			case "vision": return new ISpellEffect[]{new DiscoverRoadEffect()};
		
			case "whistle for monsters": return new ISpellEffect[]{new MoveSoundEffect()};
			case "witch's brew": return new ISpellEffect[]{new ChitChangeEffect()};
			
			// new spells
			case "duel": return new ISpellEffect[]{new DuelEffect()};
			case "fighting hands": return new ISpellEffect[]{new FightChitEffect()};
			case "flame sword": return new ISpellEffect[]{new MagicWeaponEffect()};
			case "lucky blow": return new ISpellEffect[]{new FinalChitHarmEffect()};
			case "magic shield": return new ISpellEffect[]{new MagicShieldEffect()};
			case "mystic boots": return new ISpellEffect[]{new MoveChitEffect()};
			case "rocks glow": return new ISpellEffect[]{new LightEffect()};
			case "sleep": return new ISpellEffect[]{new SleepEffect()};
			case "spider web": return new ISpellEffect[]{new SpiderWebEffect()};
			
			// super realm
			case "alter object": return new ISpellEffect[]{new AlterObjectEffect()};
			case "alter size": return new ISpellEffect[]{new AlterSizeEffect()};
			case "barkskin": return new ISpellEffect[]{new ApplyNamedEffect(Constants.BARKSKIN)};
			case "blinding light": return new ISpellEffect[]{new PhaseChitEffect()};
			case "blunt": return new ISpellEffect[]{new ApplyNamedEffect(Constants.BLUNT)};
			case "camouflage": return new ISpellEffect[]{new ApplyNamedEffect(Constants.CAMOUFLAGE)};
			case "charm snake": return new ISpellEffect[]{new ControlEffect()};
			case "control horse": return new ISpellEffect[]{new ControlHorseEffect()};
			case "control element": return new ISpellEffect[]{new ControlEffect()};
			case "dazzle": return new ISpellEffect[]{new PhaseChitEffect()};
			case "deal with orcs and goblins": return new ISpellEffect[]{new PacifyEffect(0)};
			case "dark favor": return new ISpellEffect[]{new PhaseChitEffect()};
			case "divine protection": return new ISpellEffect[]{new ApplyNamedEffect(Constants.STRENGTHENED_VULNERABILITY)};
			case "enchant key": return new ISpellEffect[]{new ApplyNamedEffect(Constants.KEY)};
			case "enchant weapon": return new ISpellEffect[]{new EnchantWeaponEffect(),new ApplyNamedEffect(Constants.IGNORE_MIST_LIKE)};
			case "free the soul": return new ISpellEffect[]{new FreeTheSoulEffect()};
			case "frozen water": return new ISpellEffect[]{new FrozenWaterEffect()};
			case "guide beast": return new ISpellEffect[]{new ControlEffect()};
			case "grow wings": return new ISpellEffect[]{new ApplyNamedEffect(Constants.GROW_WINGS)};
			case "holy shield": return new ISpellEffect[]{new PhaseChitEffect()};
			case "horse whisper": return new ISpellEffect[]{new ApplyNamedEffect(Constants.HORSE_WHISPER),new TurnLightSideUpEffect()};
			case "lift object": return new ISpellEffect[]{new ApplyNamedEffect(Constants.WEIGHT_NEGLIGIBLE)};
			case "meditate": return new ISpellEffect[]{new MeditateEffect()};
			case "mesmerize": return new ISpellEffect[]{new MesmerizeEffect()};
			case "migration": return new ISpellEffect[]{new MigrationEffect()};
			case "mountain surge": return new ISpellEffect[]{new ApplyClearingEffect(Constants.MOUNTAIN_SURGE)};
			case "negative aura": return new ISpellEffect[]{new ApplyNamedEffect(Constants.NEGATIVE_AURA)};
			case "pacify skeletons": return new ISpellEffect[]{new ApplyNamedEffectWithValues(Constants.PACIFY_MONSTER),new ApplyNamedEffectWithValue(Constants.PACIFY_TYPE)};
			case "redirect": return new ISpellEffect[]{new RedirectEffect()};
			case "reanimate": return new ISpellEffect[]{new ReanimateEffect()};
			case "reinvigorate": return new ISpellEffect[]{new ExtraActionEffect("R")};
			case "reserve": return new ISpellEffect[]{new PhaseChitEffect()};
			case "summon demon": return new ISpellEffect[]{new SummonEffect(SpellUtility.SummonType.demon.toString())};
			case "thorns": return new ISpellEffect[]{new ThornsEffect()};
			case "tracker's sense": return new ISpellEffect[]{new ApplyNamedEffect(Constants.TRACKERS_SENSE)};
			case "violent winds": return new ISpellEffect[]{new ViolentWindsEffect()};
			case "water run": return new ISpellEffect[]{new MoveChitEffect()};
			case "white feathered wing spell": return new ISpellEffect[]{new ApplyDieModEffect()};
			
			default: break;
		}
		if (alternativeSpellEffect!=null) {
			return create(alternativeSpellEffect,null);
		}
		return null;
	}
}