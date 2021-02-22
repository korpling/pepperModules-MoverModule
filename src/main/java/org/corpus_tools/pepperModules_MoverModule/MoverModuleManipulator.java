/**
 * Copyright 2016 GU.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.corpus_tools.pepperModules_MoverModule;

import java.util.HashSet;
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
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SStructuredNode;
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
                        String srcAnnoVal = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCANNOVAL, null);
                        String srcAnnoNS = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCANNONS, null);
                        String srcType = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCTYPE, "edge");
                        String srcName = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.SRCNAME, null);
                        String trgAnno = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGANNO, null);
                        String trgAnnoVal = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGANNOVAL, null);
                        String trgNS = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGNS, null);
                        String trgLayer = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGLAYER, null);
                        String trgName = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGNAME, null);
                        String trgObj = (String) getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.TRGOBJ, "target");

                        boolean removeOrig = Boolean.valueOf(getProperties().getProperties().getProperty(MoverModuleManipulatorProperties.RMORIG)); 

                        SLayer trgNodeLayer = null;
                        if (trgLayer != null){
                            trgNodeLayer = SaltFactory.createSLayer();
                            trgNodeLayer.setName(trgLayer);
                            getDocument().getDocumentGraph().addLayer(trgNodeLayer);
                        }

                        
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

                        
                        
                        if ("tok2node".equals(srcType)){
                        
                            // Get all tokens
                            List<SToken> toks = getDocument().getDocumentGraph().getTokens();
                            
                            for (SToken tok : toks){
                                // Get token annotations
                                Set<SAnnotation> annos = tok.getAnnotations();
                                Set<SAnnotation> toRemove = new HashSet<>();
                                for (SAnnotation anno : annos){
                                    if ((srcAnnoName == null || srcAnnoName.equals(anno.getName())) && (srcAnnoNS == null || srcAnnoNS.equals(anno.getNamespace()))){
                                        // This is a targeted annotation, generate the structure to move annotation to
                                        SNode n;
                                        SRelation r;
                                        if ("struct".equals(trgObj)){
                                            n = SaltFactory.createSStructure();
                                            r = SaltFactory.createSDominanceRelation();
                                            if (trgNodeLayer != null){
                                                r.addLayer(trgNodeLayer);
                                            }
                                        }
                                        else{
                                            n = SaltFactory.createSSpan();
                                            r = SaltFactory.createSSpanningRelation();
                                        }
                                        if (trgNodeLayer != null){
                                            n.addLayer(trgNodeLayer);                                            
                                        }
                                        String ns = trgNS;
                                        String ann = trgAnno;
                                        String val = trgAnnoVal;
                                        if (trgNS == null){
                                            ns = anno.getNamespace();
                                        }
                                        if (trgAnno == null){
                                            ann = anno.getName();
                                        }
                                        if (trgAnnoVal == null){
                                            val = anno.getValue_STEXT();
                                        }
                                        n.createAnnotation(ns, ann, val);
                                        r.setSource(n);
                                        r.setTarget(tok);
                                        getDocument().getDocumentGraph().addNode(n);
                                        getDocument().getDocumentGraph().addRelation(r);
                                        if (removeOrig){
                                            toRemove.add(anno);
                                        }
                                    }
                                }
                                for (SAnnotation anno : toRemove){
                                    tok.removeLabel(anno.getNamespace(), anno.getName());
                                }
                            }
                            
                        }
                        
                        else{
                            // Get all relations in the document
                            List<SRelation> rels = getDocument().getDocumentGraph().getRelations(SALT_TYPE.SPOINTING_RELATION);
                            Set<SRelation> relsToRemove = new HashSet<>();
                            Set<SRelation> relsToAdd = new HashSet<>();
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
                                            if (removeOrig){
                                                ((SNode) rel.getSource()).removeLabel(srcAnnoNS,srcAnnoName);
                                            }
                                        }
                                        else{
                                            boolean found = false;
                                            Set<SAnnotation> annos = ((SNode) rel.getSource()).getAnnotations();
                                            for (SAnnotation a : annos){
                                                if (a.getName().equals(srcAnnoName)){
                                                    annoVal = a.getValue_STEXT();
                                                    found = true;
                                                }
                                            }
                                            if (!found){
                                                continue;
                                            }
                                        }
                                        if (srcAnnoVal != null){
                                            
                                        }
                                    }
                                    else if ("edge2edge".equals(srcType)){
                                        
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
                                SNode source;
                                if ("target".equals(trgObj)){
                                    source = (SNode) rel.getSource();
                                    target = (SNode) rel.getTarget();
                                }
                                else {
                                    source = (SNode) rel.getTarget();
                                    target = (SNode) rel.getSource();
                                }
                                if (true){
                                    //throw new PepperModuleDataException(this, "lyr: " + srcLayer + "; srcnm: "+srcName + "; trgNS: " + trgNS + "; trgAnno: " + trgAnno + "; trgVal: " + trgAnnoVal + "; trgObj: " + trgObj );
                                }
                                if ("edge2edge".equals(srcType)){
                                    
                                    boolean found = srcAnnoName == null ? true : false;
                                    Set<SAnnotation> annos = rel.getAnnotations();
                                    for (SAnnotation a : annos){
                                        if (a.getName().equals(srcAnnoName)){
                                            annoVal = a.getValue_STEXT();
                                            found = true;
                                        }
                                    }
                                    if (!found){
                                        continue;
                                    }
                                    if (srcAnnoVal != null){
                                        found = false;
                                        for (SAnnotation a : annos){
                                            if (a.getValue_STEXT().matches(srcAnnoVal)){
                                                found = true;
                                            }
                                        }
                                        if (!found){
                                            continue;
                                        }
                                    }

                                    SPointingRelation newRel = SaltFactory.createSPointingRelation();
                                    newRel.setSource((SStructuredNode) source);
                                    newRel.setTarget((SStructuredNode) target);
                                    newRel.setType(trgName);
                                    for (SAnnotation ann : rel.getAnnotations()){
                                        String annNS = trgNS == null ? ann.getNamespace(): trgNS;
                                        String annName = trgAnno == null ? ann.getName() : trgAnno;
                                        String annVal = trgAnnoVal == null ? ann.getValue_STEXT() : trgAnnoVal;
                                        newRel.createAnnotation(annNS, annName, annVal);
                                    }
                                    relsToAdd.add(newRel);
                                    if (removeOrig){
                                        relsToRemove.add(rel);
                                    }
                                }
                                else{
                                    SAnnotation anno = SaltFactory.createSAnnotation();
                                    anno.setName(trgAnno);
                                    anno.setNamespace(trgNS);
                                    anno.setValue(annoVal);
                                    target.addAnnotation(anno);
                                }
                            }
                            
                            for (SRelation r : relsToRemove){
                                getDocument().getDocumentGraph().removeRelation(r);
                            }
                            for (SRelation r : relsToAdd){
                                if (trgNodeLayer != null){
                                    r.addLayer(trgNodeLayer);
                                }
                                getDocument().getDocumentGraph().addRelation(r);                                  
                            }

                            
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
