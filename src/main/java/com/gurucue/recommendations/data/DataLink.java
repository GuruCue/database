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

import com.gurucue.recommendations.entity.Partner;
import com.gurucue.recommendations.entitymanager.*;

/**
 * The data exchange interface implemented by storage back-ends.
 * THERE IS NO GUARANTEE FOR THREAD SAFETY! Use a separate instance
 * in each thread.
 */
public interface DataLink extends AutoCloseable {
    @Override
    void close();
    void commit();
    void rollback();
    DataProvider getProvider();
    Partner getPartnerZero();
    AttributeManager getAttributeManager();
    ConsumerEventManager getConsumerEventManager();
    ConsumerEventTypeManager getConsumerEventTypeManager();
    ConsumerManager getConsumerManager();
    DataTypeManager getDataTypeManager();
    LanguageManager getLanguageManager();
    LogSvcRecommendationManager getLogSvcRecommendationManager();
    PartnerManager getPartnerManager();
    PartnerRecommenderManager getPartnerRecommenderManager();
    ProductManager getProductManager();
    ProductTypeManager getProductTypeManager();
    ProductTypeAttributeManager getProductTypeAttributeManager();
    RecommenderConsumerOverrideManager getRecommenderConsumerOverrideManager();
    RecommenderManager getRecommenderManager();
    RelationTypeManager getRelationTypeManager();
    LogSvcSearchManager getLogSvcSearchManager();
}
