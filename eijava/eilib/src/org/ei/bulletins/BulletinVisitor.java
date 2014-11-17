package org.ei.bulletins;



public interface BulletinVisitor
{
	public void visitWith(BulletinPage page) throws Exception;
	public void visitWith(Bulletin bulletin) throws Exception;

}