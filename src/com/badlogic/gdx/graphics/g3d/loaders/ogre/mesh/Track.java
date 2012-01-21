/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.badlogic.gdx.graphics.g3d.loaders.ogre.mesh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"keyframes"})
@XmlRootElement(name = "track")
public class Track {

	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String target;
	@XmlAttribute
	@XmlJavaTypeAdapter(NormalizedStringAdapter.class)
	protected String index;
	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String type;
	@XmlElement(required = true)
	protected Keyframes keyframes;

	/** Gets the value of the target property.
	 * 
	 * @return possible object is {@link String } */
	public String getTarget () {
		return target;
	}

	/** Sets the value of the target property.
	 * 
	 * @param value allowed object is {@link String } */
	public void setTarget (String value) {
		this.target = value;
	}

	/** Gets the value of the index property.
	 * 
	 * @return possible object is {@link String } */
	public String getIndex () {
		if (index == null) {
			return "0";
		} else {
			return index;
		}
	}

	/** Sets the value of the index property.
	 * 
	 * @param value allowed object is {@link String } */
	public void setIndex (String value) {
		this.index = value;
	}

	/** Gets the value of the type property.
	 * 
	 * @return possible object is {@link String } */
	public String getType () {
		return type;
	}

	/** Sets the value of the type property.
	 * 
	 * @param value allowed object is {@link String } */
	public void setType (String value) {
		this.type = value;
	}

	/** Gets the value of the keyframes property.
	 * 
	 * @return possible object is {@link Keyframes } */
	public Keyframes getKeyframes () {
		return keyframes;
	}

	/** Sets the value of the keyframes property.
	 * 
	 * @param value allowed object is {@link Keyframes } */
	public void setKeyframes (Keyframes value) {
		this.keyframes = value;
	}

}