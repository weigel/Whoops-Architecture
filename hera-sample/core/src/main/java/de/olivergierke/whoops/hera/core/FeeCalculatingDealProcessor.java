/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.olivergierke.whoops.hera.core;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.synyx.hera.core.PluginRegistry;

/**
 * Sample {@link DealProcessor} implementation that accesses {@link TransactionFeeProvider}s through a Hera
 * {@link PluginRegistry}.
 * 
 * @author Oliver Gierke
 */
@Component
class FeeCalculatingDealProcessor implements DealProcessor {

	private final PluginRegistry<TransactionFeeProvider, Instrument> feeProviders;

	/**
	 * Creates a new {@link FeeCalculatingDealProcessor} from the given {@link PluginRegistry}.
	 * 
	 * @param feeProviders
	 */
	@Autowired
	public FeeCalculatingDealProcessor(PluginRegistry<TransactionFeeProvider, Instrument> feeProviders) {
		Assert.notNull(feeProviders);
		this.feeProviders = feeProviders;
	}

	/*
	 * (non-Javadoc)
	 * @see de.olivergierke.whoops.hera.core.DealProcessor#process(de.olivergierke.whoops.hera.core.Deal)
	 */
	public Result process(Deal deal) {

		TransactionFeeProvider provider = feeProviders.getPluginFor(deal.getInstrument(),
				new UnsupportedInstrumentException(deal.getInstrument()));
		BigDecimal fee = provider.getTransactionFee(deal.getInstrument());

		return new DefaultResult(fee);
	}
}