package org.datayoo.correlator;

import org.datayoo.moql.EntityMap;

public interface EventCreator {

  CorrelatorEvent create(EntityMap entityMap);

}
