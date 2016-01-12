/*
 * $Id$
 * $URL$
 *
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
 */

package org.ikasan.filter.duplicate.service;

import org.ikasan.filter.configuration.FilterConfiguration;

/**
 * Default FilteredMessageConfiguration specific to duplicate file message filtering.
 *
 * @author Ikasan Development Team
 *
 */
public class FilteredMessageConfiguration extends FilterConfiguration
{
    /** default housekeep batch size of 100 */
    private int housekeepBatchSize = 100;

    /** batched housekeeping - default true */
    private boolean batchedHousekeep = true;
    
    /** default transaction size of 1000 */
    private int transactionBatchSize = 1000;

    public int getHousekeepBatchSize() {
        return housekeepBatchSize;
    }

    public void setHousekeepBatchSize(int housekeepBatchSize) {
        this.housekeepBatchSize = housekeepBatchSize;
    }

    public boolean isBatchedHousekeep() {
        return batchedHousekeep;
    }

    public void setBatchedHousekeep(boolean batchedHousekeep) {
        this.batchedHousekeep = batchedHousekeep;
    }

	/**
	 * @return the transactionBatchSize
	 */
	public int getTransactionBatchSize()
	{
		return transactionBatchSize;
	}

	/**
	 * @param transactionBatchSize the transactionBatchSize to set
	 */
	public void setTransactionBatchSize(int transactionBatchSize)
	{
		this.transactionBatchSize = transactionBatchSize;
	}
}
