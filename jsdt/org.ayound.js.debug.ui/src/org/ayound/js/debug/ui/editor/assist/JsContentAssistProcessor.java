/*******************************************************************************
 *
 *==============================================================================
 *
 * Copyright (c) 2008-2011 ayound@gmail.com 
 * This program and the accompanying materials
 * are made available under the terms of the Apache License 2.0 
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * All rights reserved.
 * 
 * Created on 2008-11-22
 *******************************************************************************/

package org.ayound.js.debug.ui.editor.assist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class JsContentAssistProcessor implements IContentAssistProcessor {

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
//		ICompletionProposal[] result= new ICompletionProposal[]{new CompletionProposal("toString()",offset,0,"toString()".length())};
//
//
//		return result;
		return null;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		IContextInformation[] result= new IContextInformation[5];
		for (int i= 0; i < result.length; i++){			
			result[i]= new ContextInformation("test" + i,"test" + i);
		}
		return result;

	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.' };

	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '.' };
	}

	public IContextInformationValidator getContextInformationValidator() {
		System.out.println("context");
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

}
