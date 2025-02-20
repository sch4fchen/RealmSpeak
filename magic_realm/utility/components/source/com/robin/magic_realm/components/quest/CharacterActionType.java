package com.robin.magic_realm.components.quest;

public enum CharacterActionType {
	Unknown,
	ActivatingItem,
	DeactivatingItem,
	Alert,
	Cache,
	CastSpell,
	Enchant,
	Fly,
	Fortify,
	Heal,
	Hide,
	Hire,
	Move,
	Open,
	Repair,
	Rest,
	SearchTable,
	Teleport,
	Trading,
	AbandonMissionCampaign,
	CompleteMissionCampaign,
	FailMissionCampaign,
	PickUpMissionCampaign,
	;
	
	public String getDescriptor() {
		switch(this) {
			case PickUpMissionCampaign:		return "Must pick up "; 
			case AbandonMissionCampaign:	return "Must abandon "; 
			case FailMissionCampaign:		return "Must fail "; 
			case CompleteMissionCampaign:	return "Must complete ";
			default: 						return "?";
		}
	}
	public static CharacterActionType[] mcValues() {
		CharacterActionType[] mc = new CharacterActionType[4];
		mc[0] = PickUpMissionCampaign;
		mc[1] = AbandonMissionCampaign;
		mc[2] = FailMissionCampaign;
		mc[3] = CompleteMissionCampaign;
		return mc;
	}
}