using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Clicked : MonoBehaviour
{
    public static GameObject clickedGameObject;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetMouseButtonDown(0))
        {
            if(clickedGameObject != null)
            {
                clickedGameObject = null;
            }

            Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
            RaycastHit hit = new RaycastHit();

            if (Physics.Raycast(ray, out hit))
            {
                clickedGameObject = hit.collider.gameObject.transform.root.gameObject;  // 親要素の取得
                if (!clickedGameObject.CompareTag("Plan"))
                {
                    clickedGameObject = null;  // Planeは対象外
                }
            }

            Debug.Log(hit.collider.gameObject);
        }
    }
}
