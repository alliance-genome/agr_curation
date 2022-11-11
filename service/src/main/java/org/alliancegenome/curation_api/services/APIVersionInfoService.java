package org.alliancegenome.curation_api.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.constants.AGRCurationSchemaVersion;

@RequestScoped
public class APIVersionInfoService {
	
	public Boolean isPartiallyImplemented(Class<?> clazz, AGRCurationSchemaVersion version) {
		if (version.partial()) return true;
		
		Set<Class<?>> dependencyClasses = getDependencyClasses(version);
		for (Class<?> dependencyClass : dependencyClasses) {
			AGRCurationSchemaVersion dependencyVersion = dependencyClass.getAnnotation(AGRCurationSchemaVersion.class);
			if (dependencyVersion.partial()) return true;
		}
		
		return false;
	}

	public List<String> getVersionRange (AGRCurationSchemaVersion version) {
		Set<String> minVersions = new HashSet<String>();
		minVersions.add(version.min());
		Set<String> maxVersions = new HashSet<String>();
		maxVersions.add(version.max());
		
		Set<Class<?>> dependencyClasses = getDependencyClasses(version);
		for (Class<?> dependencyClass : dependencyClasses) {
			AGRCurationSchemaVersion dependencyVersion = dependencyClass.getAnnotation(AGRCurationSchemaVersion.class);
			minVersions.add(dependencyVersion.min());
			maxVersions.add(dependencyVersion.max());
		}
		
		String minVersion = "0.0.0";
		for (String classMinVersion : minVersions) {
			minVersion = minVersion.equals(getLowestVersion(minVersion, classMinVersion)) ? classMinVersion : minVersion;
		}
		
		String maxVersion = "999999.999999.999999";
		for (String classMaxVersion : maxVersions) {
			maxVersion = maxVersion.equals(getLowestVersion(maxVersion, classMaxVersion)) ? maxVersion : classMaxVersion;
		}
		
		return List.of(minVersion, maxVersion);
	}
	
	private String getLowestVersion(String version1, String version2) {
		List<Integer> vParts1 = getVersionParts(version1);
		List<Integer> vParts2 = getVersionParts(version2);
		
		if (vParts1.get(0) < vParts2.get(0)) return version1;
		if (vParts1.get(0).equals(vParts2.get(0))) {
			if (vParts1.get(1) < vParts2.get(1)) return version1;
			if (vParts1.get(1).equals(vParts2.get(1))) {
				if (vParts1.get(2) < vParts2.get(2)) return version1;
			}
		}
		return version2;
	}
	
	private List<Integer> getVersionParts(String versionString) {
		Pattern p = Pattern.compile("^(\\d+)\\.(\\d+)\\.?(\\d*)");
		Matcher m = p.matcher(versionString);
		Integer majorVersion = 0;
		Integer minorVersion = 0;
		Integer patchVersion = 0;
		if (m.find()) {
			majorVersion = m.group(1).equals("") ? 0 : Integer.parseInt(m.group(1));
			minorVersion = m.group(2).equals("") ? 0 : Integer.parseInt(m.group(2));
			patchVersion = m.group(3).equals("") ? 0 : Integer.parseInt(m.group(3));
		}
		return List.of(majorVersion, minorVersion, patchVersion);
	}

	private Set<Class<?>> getDependencyClasses(AGRCurationSchemaVersion version) {
		Set<Class<?>> dependencyClasses = new HashSet<Class<?>>();
		List<Class<?>> nextLevelDependencies = Arrays.asList(version.dependencies());
		while (nextLevelDependencies.size() > 0) {
			Set<Class<?>> newDependencies = new HashSet<Class<?>>();
			for (Class<?> dependencyClass : nextLevelDependencies) {
				dependencyClasses.add(dependencyClass);
				AGRCurationSchemaVersion dependencyVersion = dependencyClass.getAnnotation(AGRCurationSchemaVersion.class);
				newDependencies.addAll(Arrays.asList(dependencyVersion.dependencies()));
			}
			nextLevelDependencies = List.copyOf(newDependencies);
		}
		return dependencyClasses;
	}

}
