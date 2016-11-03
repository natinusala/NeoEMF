/*
 * Copyright (c) 2013-2016 Atlanmod INRIA LINA Mines Nantes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlanmod INRIA LINA Mines Nantes - initial API and implementation
 */

package fr.inria.atlanmod.neoemf.benchmarks.ase2015;

import fr.inria.atlanmod.neoemf.benchmarks.ase2015.queries.ASE2015JavaQueries;
import fr.inria.atlanmod.neoemf.benchmarks.cdo.EmbeddedCDOServer;
import fr.inria.atlanmod.neoemf.benchmarks.util.CommandLineUtil;
import fr.inria.atlanmod.neoemf.benchmarks.util.MessageUtil;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.java.TextElement;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static fr.inria.atlanmod.neoemf.benchmarks.util.CommandLineUtil.Key.EPACKAGE_CLASS;
import static fr.inria.atlanmod.neoemf.benchmarks.util.CommandLineUtil.Key.IN;
import static fr.inria.atlanmod.neoemf.benchmarks.util.CommandLineUtil.Key.REPO_NAME;

public class CdoQueryGetBranchStatements {

    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            Map<String, String> cli = processCommandLineArgs(args);

            String repositoryDir = cli.get(IN);
            String repositoryName = cli.get(REPO_NAME);

            Class<?> inClazz = CdoQueryGetBranchStatements.class.getClassLoader().loadClass(cli.get(EPACKAGE_CLASS));
            inClazz.getMethod("init").invoke(null);

            try (EmbeddedCDOServer server = new EmbeddedCDOServer(repositoryDir, repositoryName)) {
                server.run();
                CDOSession session = server.openSession();
                CDOTransaction transaction = session.openTransaction();
                Resource resource = transaction.getRootResource();
                {
                    Runtime.getRuntime().gc();
                    long initialUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    LOG.info("Used memory before query: {0}", MessageUtil.byteCountToDisplaySize(initialUsedMemory));
                    LOG.info("Start query");
                    Instant begin = Instant.now();
                    Set<TextElement> list = ASE2015JavaQueries.getCommentsTagContent(resource);
                    Instant end = Instant.now();
                    LOG.info("End query");
                    LOG.info("Query result contains {0} elements", list.size());
                    LOG.info("Time spent: {0}", Duration.between(begin, end));
                    Runtime.getRuntime().gc();
                    long finalUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    LOG.info("Used memory after query: {0}", MessageUtil.byteCountToDisplaySize(finalUsedMemory));
                    LOG.info("Memory use increase: {0}", MessageUtil.byteCountToDisplaySize(finalUsedMemory - initialUsedMemory));
                }

                transaction.close();
                session.close();
            }
        }
        catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    private static Map<String, String> processCommandLineArgs(String... args) throws ParseException {
        Options options = new Options();

        options.addOption(Option.builder(IN)
                .argName("INPUT")
                .desc("Input CDO resource directory")
                .hasArg()
                .required()
                .build());

        options.addOption(Option.builder(EPACKAGE_CLASS)
                .argName("CLASS")
                .desc("FQN of EPackage implementation class")
                .hasArg()
                .required()
                .build());

        options.addOption(Option.builder(REPO_NAME)
                .argName("REPO_NAME")
                .desc("CDO Repository name")
                .hasArg()
                .required()
                .build());

        return CommandLineUtil.getValues(options, args);
    }
}
