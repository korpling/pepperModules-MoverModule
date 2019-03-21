/*
 * Copyright 2017 GU.
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
package org.corpus_tools.pepperModules_MoverModule;
import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;
/**
 *
 * @author Amir Zeldes
 */
public class MoverModuleManipulatorProperties extends PepperModuleProperties {
    

        public static final String PREFIX = "Mover.";


        public final static String SRCTYPE = PREFIX + "sourceType";
        public final static String SRCNAME = PREFIX + "sourceName";
        public final static String SRCLAYER = PREFIX + "sourceLayer";
        public final static String SRCANNO = PREFIX + "sourceAnno";
        public final static String SRCANNOVAL = PREFIX + "sourceAnnoValue";
        public final static String SRCANNONS = PREFIX + "sourceAnnoNamespace";
        public final static String TRGOBJ = PREFIX + "targetObject";
        public final static String TRGNS = PREFIX + "targetNamespace";
        public final static String TRGANNO = PREFIX + "targetAnno";
        public final static String TRGANNOVAL = PREFIX + "targetAnnoVal";
        public final static String TRGLAYER = PREFIX + "targetLayer";
        public final static String TRGNAME = PREFIX + "targetName";
        public final static String RMORIG = PREFIX + "removeOrig";

        
	public MoverModuleManipulatorProperties() {
            
		this.addProperty(new PepperModuleProperty<String>(SRCTYPE, String.class, "Specifies the source object type, e.g. 'edge'.", "edge", false));
		this.addProperty(new PepperModuleProperty<String>(SRCNAME, String.class, "Specifies the source object's type name, e.g. 'dep' for an edge named 'dep'.", null, false));
		this.addProperty(new PepperModuleProperty<String>(SRCLAYER, String.class, "Specifies the source object layer, any layer if null.", null, false));
		this.addProperty(new PepperModuleProperty<String>(SRCANNO, String.class, "Specifies the source annotation to move.", null, false));
		this.addProperty(new PepperModuleProperty<String>(SRCANNOVAL, String.class, "Specifies the source annotation value to move as a regular expression.", null, false));
		this.addProperty(new PepperModuleProperty<String>(SRCANNONS, String.class, "Specifies the source annotation namespace, any namespace if null", null, false));
		this.addProperty(new PepperModuleProperty<String>(TRGOBJ, String.class, "The target to move to; if source is an edge, one of {source, target}.", "target", false));
		this.addProperty(new PepperModuleProperty<String>(TRGNS, String.class, "Namespace to add to target annotation.", "default_ns", false));
		this.addProperty(new PepperModuleProperty<String>(TRGANNO, String.class, "Name for target annotation, identical to source by default.", null, false));
		this.addProperty(new PepperModuleProperty<String>(TRGANNOVAL, String.class, "A fixed value for target annotation, if not derived from a source annotation.", null, false));
		this.addProperty(new PepperModuleProperty<String>(TRGLAYER, String.class, "Name for the layer of generated target objects (for tok2node).", null, false));
		this.addProperty(new PepperModuleProperty<String>(TRGNAME, String.class, "Specifies the target object's type name, e.g. 'deprel' to create an edge named 'deprel' in edge2edge mode.", null, false));
		this.addProperty(new PepperModuleProperty<Boolean>(RMORIG, Boolean.class, "Whether to remove the original annotation after moving.", false, false));
        }
        
        public boolean getRemoveOrig(){
            return ((Boolean) getProperty(RMORIG).getValue());
        }


}
