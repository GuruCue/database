/*
 * This file is part of Guru Cue Search & Recommendation Engine.
 * Copyright (C) 2017 Guru Cue Ltd.
 *
 * Guru Cue Search & Recommendation Engine is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Guru Cue Search & Recommendation Engine is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Guru Cue Search & Recommendation Engine. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.gurucue.recommendations.data;

import com.gurucue.recommendations.entity.Language;
import com.gurucue.recommendations.entitymanager.LanguageManager;

import java.util.HashMap;
import java.util.Map;

public final class LanguageCodes {
    public final Language unknown;

    public final long idForUknown;

    private final Map<String, Language> iso639_2tMapping = new HashMap<>();
    private final Map<String, Language> iso639_1Mapping = new HashMap<>();

    public LanguageCodes(final LanguageManager manager) {
        unknown = manager.getByIdentifier(Language.UNKNOWN);
        idForUknown = unknown.getId();

        for (final Language language: manager.list()) {
            iso639_2tMapping.put(language.getIso639_2t(), language);
            iso639_1Mapping.put(language.getIso639_1(), language);
        }
    }

    public final Language byIso639_2t(final String identifier) {
        return iso639_2tMapping.get(identifier);
    }

    public final Language byIso639_1(final String identifier) {
        return iso639_1Mapping.get(identifier);
    }
}
