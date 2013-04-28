package com.search.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.search.data.Document;
import com.search.data.Field;
import com.search.data.Token;
/*
 * �������������ϲ������ڹ���
 */
public class SimpleMergeIndex {
	private LinkedList<Document> documents=new LinkedList<Document>();
	private LinkedList<Field> fields = new LinkedList<Field>();
	private LinkedList<Token_Structure> tokens_structure ;
	
	public SimpleMergeIndex(LinkedList<Document> documents){
		this.documents=documents;
	}
	public LinkedList<Token_Structure> getToken_Structure() {
		return tokens_structure;
	}

	public LinkedList<Document> getDocuments(){
		return documents;
	}
	
	public LinkedList<Field> getField() {
		return fields;
	}

	//��Ҫ�ĺ��ķ���
	public void mergeIndex() throws Exception{
		Sort();
	}
	
	// ���ļ��еõ�tokensȻ��ϲ�����
	public void Sort() throws Exception {
		LinkedList<LinkedList<Token>> t = new LinkedList<LinkedList<Token>>();
//		// ���ļ��еõ�tokens
		
		for (Document document:documents) {
			BuildIndex buildindex=new SimpleBuildIndex(document);
			Index index=buildindex.buildIndex();
			
				
			//��Field����fields
			for(Field f:index.getFields()){
				fields.addLast(f);
			}
				
			LinkedList<Token> temp=new LinkedList<Token>();
			//��token����tokens
			for(Token token:index.getTokens()){
				temp.addLast(token);
			}
			
			temp=TokenSort.Sort(temp);
			t.addLast(temp);					
		}
		
		// ���кϲ�����
		while(t.size()!=1){
			LinkedList<LinkedList<Token>> list = new LinkedList<LinkedList<Token>>();
			list = this.Merge(t);
			t.clear();
			while (!list.isEmpty()) {
				t.addLast(list.pollFirst());
			}
		}

		// ת��Ϊһ���ļ�����������
		LinkedList<Token> index_list = new LinkedList<Token>();
		Iterator<LinkedList<Token>> iterator_list = t.iterator();
		while (iterator_list.hasNext()) {
			Iterator<Token> iterator = iterator_list.next().iterator();
			while (iterator.hasNext()) {
				index_list.add(iterator.next());
			}
		}
		LinkedList<Token_Structure> indexs = getIndexs(index_list);
		tokens_structure = indexs;
	}

	// ��һ����СΪn������linkedlist<LinkedList<Token>> �ϲ�Ϊһ����СΪ2/n��LinkedList<LinkedList<Token>>
	private LinkedList<LinkedList<Token>> Merge(
			LinkedList<LinkedList<Token>> tokens){
		LinkedList<LinkedList<Token>> t = new LinkedList<LinkedList<Token>>();
		
		//����ܱ�2����
		if(tokens.size()%2==0){
			while (!tokens.isEmpty()) {
				t.addLast(TokenSort.MergeSort(tokens.pollFirst(), tokens.pollLast()));
			}
		}
		
		else{
			for(int i=0;i<tokens.size()/2;i++){
				t.addLast(TokenSort.MergeSort(tokens.pollFirst(), tokens.pollLast()));
			}
			t.addLast(tokens.pollLast());
			
		}
	
		return t;
	}

	// �õ������ṹ
	private LinkedList<Token_Structure> getIndexs(LinkedList<Token> linkedList) {
		LinkedList<Token_Structure> index_list = new LinkedList<Token_Structure>();
		while (!linkedList.isEmpty()) {
			Token token = linkedList.pollFirst();
			Token_Structure index = new Token_Structure(token.getTerm());
			index.add(token.getID());
			index_list.addLast(index);
			int frequency = 1;// ���ô�Ƶ
			while (!linkedList.isEmpty()
					&& linkedList.peekFirst().getTerm().equals(token.getTerm())) {
				index.add(linkedList.pollFirst().getID());
				frequency++;
			}
			index.setFrequency(frequency);
		}
		return index_list;
	}

	// ������д���ض����ļ���
	public void write_index_to_file(String dirpath) throws IOException {
//		FileOutputStream out = new FileOutputStream(file);
//		Iterator<Token_Structure> iterator = this.tokens_structure.iterator();
//		while (iterator.hasNext()) {
//			Token_Structure index = iterator.next();
//			out.write("Term:".getBytes());
//			out.write(index.getTerm().getBytes());
//			out.write("\n".getBytes());
//			while (!index.isEmpty()) {
//				out.write(String.valueOf(index.pollFirst()).getBytes());
//				out.write("\n".getBytes());
//			}
//		}
//		out.close();
	}

	// ��ӡ����
	public void printIndex() throws Exception {
		Iterator<Token_Structure> iterator = tokens_structure.iterator();
		while (iterator.hasNext()) {
			Token_Structure index = iterator.next();
			System.out.print(index.getTerm() + ":");
			Iterator<Long> index_iterator = index.Iterator();
			while (index_iterator.hasNext()) {
				System.out.print(index_iterator.next() + "   ");
			}
			System.out.println();
		}
	}

}