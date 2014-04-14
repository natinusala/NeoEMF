package fr.inria.atlanmod.kyanos.benchmarks;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
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
import fr.inria.atlanmod.kyanos.core.impl.KyanosResourceImpl;
import fr.inria.atlanmod.kyanos.util.KyanosURI;

public class KyanosTraverser {

	private static final Logger LOG = Logger.getLogger(KyanosTraverser.class.getName());
	
	private static final String IN = "input";

	private static final String EPACKAGE_CLASS = "epackage_class";

	private static final String OPTIONS_FILE = "options_file";

	public static void main(String[] args) {
		Options options = new Options();
		
		Option inputOpt = OptionBuilder.create(IN);
		inputOpt.setArgName("INPUT");
		inputOpt.setDescription("Input Kyanos resource directory");
		inputOpt.setArgs(1);
		inputOpt.setRequired(true);
		
		Option inClassOpt = OptionBuilder.create(EPACKAGE_CLASS);
		inClassOpt.setArgName("CLASS");
		inClassOpt.setDescription("FQN of EPackage implementation class");
		inClassOpt.setArgs(1);
		inClassOpt.setRequired(true);
		
		Option optFileOpt = OptionBuilder.create(OPTIONS_FILE);
		optFileOpt.setArgName("FILE");
		optFileOpt.setDescription("Properties file holding the options to be used in the Kyanos Resource");
		optFileOpt.setArgs(1);
		
		options.addOption(inputOpt);
		options.addOption(inClassOpt);
		options.addOption(optFileOpt);

		CommandLineParser parser = new PosixParser();
		
		try {
			CommandLine commandLine = parser.parse(options, args);
			
			URI uri = KyanosURI.createKyanosURI(new File(commandLine.getOptionValue(IN)));

			Class<?> inClazz = KyanosTraverser.class.getClassLoader().loadClass(commandLine.getOptionValue(EPACKAGE_CLASS));
			inClazz.getMethod("init").invoke(null);
			
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(KyanosURI.KYANOS_SCHEME, KyanosResourceFactory.eINSTANCE);
			
			Resource resource = resourceSet.createResource(uri);
			
			Map<String, Object> loadOpts = new HashMap<String, Object>();

			if (commandLine.hasOption(OPTIONS_FILE)) {
				Properties properties = new Properties();
				properties.load(new FileInputStream(new File(commandLine.getOptionValue(OPTIONS_FILE))));
				for (final Entry<Object, Object> entry : properties.entrySet()) {
			        loadOpts.put((String) entry.getKey(), (String) entry.getValue());
			    }
			}
			resource.load(loadOpts);

			LOG.log(Level.INFO, "Start counting");
			int count = 0;
			long begin = System.currentTimeMillis();
			for (Iterator<EObject> iterator = resource.getAllContents(); iterator.hasNext(); iterator.next(), count++);
			long end = System.currentTimeMillis();
			LOG.log(Level.INFO, "End counting");
			LOG.log(Level.INFO, MessageFormat.format("Resource {0} contains {1} elements", uri, count));
			LOG.log(Level.INFO, MessageFormat.format("Time spent: {0}", MessageUtil.formatMillis(end-begin)));
			
			if (resource instanceof KyanosResourceImpl) {
				KyanosResourceImpl.shutdownWithoutUnload((KyanosResourceImpl) resource); 
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
