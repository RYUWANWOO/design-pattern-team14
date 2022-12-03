package com.holub.life;

import java.awt.*;
import javax.swing.*;

import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.model.DummyCell;
import com.holub.life.universe_settings.SampleSetting;
import com.holub.life.universe_settings.SettingA;
import com.holub.life.view.UniverseView;
import com.holub.ui.MenuSite;

/*******************************************************************
 * An implemenation of Conway's Game of Life.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame
{
	public static void main( String[] arguments )
	{	new Life();
	}

	private Life() {
		//제목을 정해요 - 건들 필요 없고
		super( "The Game of Life. "
					+"(c)2003 Allen I. Holub <http://www.holub.com>");

		//menusite로 시작을 한다 -> 수정해야 할 게 menusite라는 것을 알 수 있다.


		MenuSite.establish( this );		//{=life.java.establish}

		Clock clock = Clock.getInstance();
		DummyCell dummyCell = DummyCell.getInstance();
		Universe universe = Universe.getInstance();
		UniverseView universeview = UniverseView.getInstance();

		setDefaultCloseOperation	( EXIT_ON_CLOSE 		);
		getContentPane().setLayout	( new BorderLayout()	);
		getContentPane().add( universeview, BorderLayout.CENTER); //{=life.java.install}

		//리스너를 더해요
		SampleSetting now_setting = new SettingA();
		now_setting.establish();

		//pack()은 프레임내에 서브컴포넌트들의 레이아웃과 Prefered Size에
		//맞도록 윈도우의 사이즈를 맞추는 작업이다. -> 딱히 건들 필요 없다
		pack();
		//visible도 건들 필요가 없다.
		setVisible( true );
	}
}
