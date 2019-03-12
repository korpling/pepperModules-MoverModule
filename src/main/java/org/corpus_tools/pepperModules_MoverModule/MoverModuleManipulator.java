package org.corpus_tools.pepperModules_MoverModule;

import java.util.List;
import java.util.Set;

import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.impl.PepperManipulatorImpl;
import org.corpus_tools.pepper.impl.PepperMapperImpl;
import org.corpus_tools.pepper.modules.PepperManipulator;
import org.corpus_tools.pepper.modules.PepperMapper;
import org.corpus_tools.pepper.modules.PepperModule;
import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleDataException;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleNotReadyException;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.graph.Identifier;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

/**
 * This is a {@link PepperManipulator} which can move annotations, for 
 * example from edges to their attached source or target nodes.
 * 
 * @author Amir_Zeldes
 */
@Component(name = "MoverModuleManipulatorComponent", factory = "PepperManipulatorComponentFactory")
public class MoverModuleManipulator extends PepperManipulatorImpl {

    public MoverModuleManipulator() {
		super();
		setName("Mover");
		setSupplierContact(URI.createURI(PepperConfiguration.EMAIL));
		setSupplierHomepage(URI.createURI(PepperConfiguration.HOMEPAGE));
		setDesc("This manipulator moves annotations between objects, such as from edges to nodes");
	}

	/**
	 * @param Identifier
	 *            {@link Identifier} of the {@link SCorpus} or {@link SDocument}
	 *            to be processed.
	 * @return {@link PepperMapper} object to do the mapping task for object
	 *         connected to given {@link Identifier}
	 */
	public PepperMapper createPepperMapper(Identifier Identifier) {
		MoverModuleMapper mapper = new MoverModuleMapper();
		return (mapper);
	}


	public static class MoverModuleMapper extends PepperMapperImpl implements GraphTraverseHandler {

                @Override
		public DOCUMENT_STATUS mapSCorpus() {
			return (DOCUMENT_STATUS.COMPLETED);
		}

		@Override
		public DOCUMENT_STATUS mapSDocument() {

                        //MoverModuleManipulatorProperties properties = (MoverModuleManipulatorProperties) this.getProperties();                    
                        
                        // set up module properties
                        String srcLayer = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCLAYER, null);
                        String srcAnnoName = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCANNO, null);
                        String srcAnnoNS = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCANNONS, null);
                        String srcType = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCTYPE, "edge");
                        String srcName = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCNAME, "dep");
                        String trgAnno = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGANNO, null);
                        String trgAnnoVal = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGANNOVAL, null);
                        String trgNS = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGNS, "default_ns");
                        String trgObj = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGOBJ, "target");
                        
                        
                        if (trgAnno == null){
                            if (srcAnnoName != null){
                                trgAnno = srcAnnoName; 
                            }
                            else{
                                trgAnno = srcName;
                            }
                        }

                        if (true){
                            //throw new PepperModuleDataException(this, "lyr: " + srcLayer + "; srcnm: "+srcName + "; trgNS: " + trgNS + "; trgAnno: " + trgAnno + "; trgVal: " + trgAnnoVal + "; trgObj: " + trgObj );
                        }

                        
                            // Get all relations in the document
                            List<SRelation> rels = getDocument().getDocumentGraph().getRelations(SALT_TYPE.SPOINTING_RELATION);
                            for (SRelation rel: rels){
                                // Check source layer
                                if (srcLayer != null){
                                    boolean found = false;
                                    Set<SLayer> layers = rel.getLayers();
                                    for (SLayer layer : layers){
                                        if (layer.getName().equals(srcLayer)){
                                            found = true;
                                        }
                                    }
                                    if (!found){
                                        continue;
                                    }
                                }
                                // Check edge type, e.g. dep
                                if (srcName != null){
                                    if (rel.getType() == null){
                                        continue;
                                    }
                                    else if (!rel.getType().equals(srcName)){
                                        continue;
                                    }
                                }                                
                                // Check edge annotation
                                String annoVal = "";
                                if (srcAnnoName != null){
                                    if ("edge".equals(srcType)){
                                        SAnnotation srcAnno = rel.getAnnotation(srcAnnoNS,srcAnnoName);
                                        if (srcAnno == null){
                                            continue;                                    
                                        }
                                        else{
                                            annoVal = srcAnno.getValue_STEXT();
                                        }
                                    }
                                    else if ("source2target".equals(srcType)){
                                        if (srcAnnoName.equals("tok")) { // Source annotation is a token's text value
                                            if (!(rel.getSource() instanceof SToken)){
                                                continue;
                                            }
                                            else{
                                                annoVal = getDocument().getDocumentGraph().getText((SNode) rel.getSource());
                                            }
                                        }
                                        else if (srcAnnoNS != null){
                                            SAnnotation srcAnno = ((SNode) rel.getSource()).getAnnotation(srcAnnoNS,srcAnnoName);
                                            if (srcAnno == null){
                                                continue;                                    
                                            }
                                            else{
                                                annoVal = srcAnno.getValue_STEXT();
                                            }
                                        }
                                        else{
                                            boolean found = false;
                                            Set<SAnnotation> annos = ((SNode) rel.getSource()).getAnnotations();
                                            for (SAnnotation a : annos){
                                                if (a.getName().equals(srcAnnoName)){
                                                    annoVal = a.getValue_STEXT();                                                    
                                                }
                                            }
                                            if (!found){
                                                continue;
                                            }
                                        }
                                    }
                                    else{
                                        throw new PepperModuleDataException(this, "Unknown move type: " + srcType +". Move type should be one of: edge, source2target");
                                    }                                    
                                }
                                else if (trgAnnoVal != null){
                                    annoVal = trgAnnoVal;
                                }
                                else{
                                    throw new PepperModuleDataException(this, "No source annotation or fixed target annotation set - cannot assign null annotation");
                                }
                                SNode target;
                                if ("target".equals(trgObj)){
                                    target = (SNode) rel.getTarget();
                                }
                                else {
                                    target = (SNode) rel.getSource();
                                }
                        if (true){
                            //throw new PepperModuleDataException(this, "lyr: " + srcLayer + "; srcnm: "+srcName + "; trgNS: " + trgNS + "; trgAnno: " + trgAnno + "; trgVal: " + trgAnnoVal + "; trgObj: " + trgObj );
                        }
                                SAnnotation anno = SaltFactory.createSAnnotation();
                                anno.setName(trgAnno);
                                anno.setNamespace(trgNS);
                                anno.setValue(annoVal);
                                target.addAnnotation(anno);
                            }
  
			return (DOCUMENT_STATUS.COMPLETED);
		}

		/**
		 * This method is called for each node in document-structure, as long as
		 * {@link #checkConstraint(GRAPH_TRAVERSE_TYPE, String, SRelation, SNode, long)}
		 * returns true for this node. <br/>
		 */
		@Override
		public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode, SRelation sRelation, SNode fromNode, long order) {

		}

		/**
		 * This method is called on the way back, in depth first mode it is
		 * called for a node after all the nodes belonging to its subtree have
		 * been visited. <br/>
		 * In our dummy implementation, this method is not used.
		 */
		@Override
		public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode, SRelation edge, SNode fromNode, long order) {
		}

		/**
		 * With this method you can decide if a node is supposed to be visited
		 * by methods
		 * {@link #nodeReached(GRAPH_TRAVERSE_TYPE, String, SNode, SRelation, SNode, long)}
		 * and
		 * {@link #nodeLeft(GRAPH_TRAVERSE_TYPE, String, SNode, SRelation, SNode, long)}
		 * . In our dummy implementation for instance we do not need to visit
		 * the nodes {@link STextualDS}.
		 */
		@Override
		public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SRelation edge, SNode currNode, long order) {
			if (currNode instanceof STextualDS) {
				return (false);
			} else {
				return (true);
			}
		}
	}

	/**
	 * This method is called by the pepper framework after initializing this
	 * object and directly before start processing. Initializing means setting
	 * properties {@link PepperModuleProperties}, setting temporary files,
	 * resources etc. . returns false or throws an exception in case of
	 * {@link PepperModule} instance is not ready for any reason.
	 * 
	 * @return false, {@link PepperModule} instance is not ready for any reason,
	 *         true, else.
	 */
	@Override
	public boolean isReadyToStart() throws PepperModuleNotReadyException {
		// TODO make some initializations if necessary
		//return (super.isReadyToStart());
                return true;
	}
        
        
       
}
