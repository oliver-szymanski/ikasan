/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
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
 * ====================================================================
 */
package org.ikasan.component.endpoint.ftp.endpoint;


import static org.junit.Assert.*;

import org.ikasan.component.endpoint.common.*;
import org.ikasan.component.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.component.endpoint.persistence.dao.BaseFileTransferDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This test class supports the <code>ScheduledConsumer</code> class.
 *
 * @author Ikasan Development Team
 */
public class FtpEndpointImplTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    final FtpConsumerConfiguration mockConsumerConfiguration =
            mockery.mock(FtpConsumerConfiguration.class, "mockFtpConsumerConfiguration");

    final FileTransferClient mockFileTransferClient = mockery.mock(FileTransferClient.class);

    final BaseFileTransferDao mockBaseFileTransferDao = mockery.mock(BaseFileTransferDao.class);

    final String clientID = "testClientId";

    final String sourceDir = "srcDir";

    final String filenamePattern = "[a-z].txt";

    final String filename = "a.txt";

    final long minAge = 120;


    /**
     * Ftp Endpoint class under test.
     */
    private FtpEndpoint uut;


    @Test
    public void getFile_when_empty_list_list_of_files_returned_by_ftp_client() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setuo
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, false, false, false);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertNull("Get file returned not a null value", result);
        mockery.assertIsSatisfied();
    }

    @Test
    public void getFile_when_null_is_returned_by_ftp_client() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(null));

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, false, false, false);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertNull("Get file returned not a null value", result);
        mockery.assertIsSatisfied();
    }


    @Test
    public void getFile_when_single_file_is_returned_by_ftp_client() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry(filename);
        fileList.add(fileToDiscover);

        final BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord();

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));
                exactly(1).of(mockFileTransferClient).get(fileToDiscover);
                will(returnValue(record));

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, false, false, false);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertEquals(record, result);
        mockery.assertIsSatisfied();
    }


    @Test
    public void getFile_when_single_file_is_returned_by_ftp_client_and_file_does_not_match_pattern() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        String unmatchedFileName = "a.csv";
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry(unmatchedFileName);
        fileList.add(fileToDiscover);

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, false, false, false);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertNull("Returned file name did not match the pattern" + filenamePattern + " : ", result);
        mockery.assertIsSatisfied();
    }

    @Test
    public void getFile_when_single_file_is_returned_by_ftp_client_and_file_is_just_created() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        Date now = new Date();

        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry(filename, now);
        fileList.add(fileToDiscover);

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, false, false, false);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertNull("Returned file last accessed date is now:", result);
        mockery.assertIsSatisfied();
    }


    @Test
    public void getFile_when_single_file_is_returned_by_ftp_client_and_file_is_a_sym_link() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry(filename);
        fileToDiscover.isLink(true);
        fileList.add(fileToDiscover);

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, false, false, false);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertNull("Returned file was a symlink : ", result);
        mockery.assertIsSatisfied();
    }

    @Test
    public void getFile_when_single_file_is_returned_by_ftp_client_and_file_is_a_directory() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry(filename);
        fileToDiscover.isDirectory(true);
        fileList.add(fileToDiscover);

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, false, false, false);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertNull("Returned file was a directory : ", result);
        mockery.assertIsSatisfied();
    }


    @Test
    public void getFile_when_single_file_is_returned_by_ftp_client_and_filterOnDuplicate_is_true_and_filterByName() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        final boolean filterOnDuplicate = true;
        final boolean filterOnFilename = true;
        final boolean filterOnLastModifiedDate = false;
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry(filename);
        fileList.add(fileToDiscover);

        final BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord();

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));

                exactly(1).of(mockBaseFileTransferDao).isDuplicate(fileToDiscover, filterOnFilename, filterOnLastModifiedDate);
                will(returnValue(false));

                exactly(1).of(mockFileTransferClient).get(fileToDiscover);
                will(returnValue(record));

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, filterOnDuplicate, filterOnFilename, false);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertEquals(record, result);
        mockery.assertIsSatisfied();
    }


    @Test
    public void getFile_when_single_file_is_returned_by_ftp_client_and_filterOnDuplicate_is_true_and_filterOnFilename_is_true() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        final boolean filterOnDuplicate = true;
        final boolean filterOnFilename = true;
        final boolean filterOnLastModifiedDate = false;
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry(filename);
        fileList.add(fileToDiscover);

        final BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord();

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));

                exactly(1).of(mockBaseFileTransferDao).isDuplicate(fileToDiscover, filterOnFilename, filterOnLastModifiedDate);
                will(returnValue(true));

                exactly(0).of(mockFileTransferClient).get(fileToDiscover);

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, filterOnDuplicate, filterOnFilename, filterOnLastModifiedDate);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertNull("Returned file was a duplicate based on filename : ", result);
        mockery.assertIsSatisfied();
    }

    @Test
    public void getFile_when_single_file_is_returned_by_ftp_client_and_filterOnDuplicate_is_true_and_filterOnLastModifiedDate_is_true() throws
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException {

        // setup
        final boolean filterOnDuplicate = true;
        final boolean filterOnFilename = false;
        final boolean filterOnLastModifiedDate = true;
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry(filename);
        fileList.add(fileToDiscover);

        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFileTransferClient).ensureConnection();
                exactly(1).of(mockFileTransferClient).ls(sourceDir);
                will(returnValue(fileList));

                exactly(1).of(mockBaseFileTransferDao).isDuplicate(fileToDiscover, filterOnFilename, filterOnLastModifiedDate);
                will(returnValue(true));

                exactly(0).of(mockFileTransferClient).get(fileToDiscover);

            }
        });

        uut = new FtpEndpointImpl(mockFileTransferClient, mockBaseFileTransferDao, clientID, sourceDir,
                filenamePattern, minAge, filterOnDuplicate, filterOnFilename, filterOnLastModifiedDate);

        // test
        BaseFileTransferMappedRecord result = uut.getFile();

        // assert
        assertNull("Returned file was duplicate based on file LastModifiedDate  : ", result);
        mockery.assertIsSatisfied();
    }

}
