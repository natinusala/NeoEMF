/*******************************************************************************
 * Copyright (c) 2014 Abel G�mez.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Abel G�mez - initial API and implementation
 ******************************************************************************/
package fr.inria.atlanmod.kyanos.benchmarks;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import fr.inria.atlanmod.kyanos.benchmarks.util.MessageUtil;
import fr.inria.atlanmod.kyanos.core.KyanosResourceFactory;
import fr.inria.atlanmod.kyanos.core.impl.KyanosHbaseResourceImpl;
import fr.inria.atlanmod.kyanos.util.KyanosURI;

public class KyanosHbaseTraverser {

	private static final Logger LOG = Logger.getLogger(KyanosHbaseTraverser.class.getName());
	
	private static final String IN = "input";

	private static final String EPACKAGE_CLASS = "epackage_class";

	public static void main(String[] args) {
		Options options = new Options();
		
		Option inputOpt = OptionBuilder.create(IN);
		inputOpt.setArgName("INPUT");
		inputOpt.setDescription("Input HBase resource URI");
		inputOpt.setArgs(1);
		inputOpt.setRequired(true);
		
		Option inClassOpt = OptionBuilder.create(EPACKAGE_CLASS);
		inClassOpt.setArgName("CLASS");
		inClassOpt.setDescription("FQN of EPackage implementation class");
		inClassOpt.setArgs(1);
		inClassOpt.setRequired(true);
		
		options.addOption(inputOpt);
		options.addOption(inClassOpt);

		CommandLineParser parser = new PosixParser();
		
		try {
			CommandLine commandLine = parser.parse(options, args);
			
			URI uri = URI.createURI(commandLine.getOptionValue(IN));

			Class<?> inClazz = KyanosHbaseTraverser.class.getClassLoader().loadClass(commandLine.getOptionValue(EPACKAGE_CLASS));
			inClazz.getMethod("init").invoke(null);
			
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(KyanosURI.KYANOS_HBASE_SCHEME, KyanosResourceFactory.eINSTANCE);
			
			Resource resource = resourceSet.createResource(uri);
			
			Map<String, Object> loadOpts = new HashMap<String, Object>();
			resource.load(loadOpts);

			LOG.log(Level.INFO, "Start counting");
			int count = 0;
			long begin = System.currentTimeMillis();
			for (Iterator<EObject> iterator = resource.getAllContents(); iterator.hasNext(); iterator.next(), count++);
			long end = System.currentTimeMillis();
			LOG.log(Level.INFO, "End counting");
			LOG.log(Level.INFO, MessageFormat.format("Resource {0} contains {1} elements", uri, count));
			LOG.log(Level.INFO, MessageFormat.format("Time spent: {0}", MessageUtil.formatMillis(end-begin)));
			
			if (resource instanceof KyanosHbaseResourceImpl) {
				KyanosHbaseResourceImpl.shutdownWithoutUnload((KyanosHbaseResourceImpl) resource); 
			} else {
				resource.unload();
			}
			
		} catch (ParseException e) {
			MessageUtil.showError(e.toString());
			MessageUtil.showError("Current arguments: " + Arrays.toString(args));
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar <this-file.jar>", options, true);
		} catch (Throwable e) {
			MessageUtil.showError(e.toString());
		}
	}
	

}
