package com.robin.magic_realm.components.quest;

import com.robin.magic_realm.components.PathDetail;

public enum RoadDiscoveryType {
	SecretPassages, HiddenPaths, PathsOrPassages, ;

	public boolean matchesSecretPassages() {
		return this == SecretPassages || this == PathsOrPassages;
	}

	public boolean matchesHiddenPaths() {
		return this == HiddenPaths || this == PathsOrPassages;
	}

	public boolean matches(PathDetail path) {
		return (path.isSecret() && matchesSecretPassages()) || (path.isHidden() && matchesHiddenPaths());
	}
}